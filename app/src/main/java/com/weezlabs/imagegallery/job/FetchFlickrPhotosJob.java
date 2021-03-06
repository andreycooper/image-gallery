package com.weezlabs.imagegallery.job;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.path.android.jobqueue.Params;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.Photos;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.service.flickr.FlickrException;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.tool.Events.FetchCompletedEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import static com.weezlabs.imagegallery.db.FlickrContentProvider.AUTHORITY;
import static com.weezlabs.imagegallery.db.FlickrContentProvider.PHOTOS_CONTENT_URI;

public class FetchFlickrPhotosJob extends BaseFlickrJob {
    private static final AtomicInteger sJobCounter = new AtomicInteger(0);
    public static final String GROUP_ID = "fetch-photos";
    public static final int MILLIS = 1000;

    private final int mJobId;

    public FetchFlickrPhotosJob(FlickrStorage flickrStorage, FlickrService flickrService) {
        super(new Params(Priority.MID).requireNetwork().groupBy(GROUP_ID), flickrStorage, flickrService);
        mJobId = sJobCounter.incrementAndGet();
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if (mJobId != sJobCounter.get()) {
            //looks like other fetch jobs has been added after me. no reason to keep fetching
            //many times, cancel me, let the other one fetch photos.
            return;
        }

        User user = getFlickrStorage().restoreFlickrUser();
        if (user != null) {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            List<Photo> photoList = new ArrayList<>();

            Cursor cursor = resolver.query(PHOTOS_CONTENT_URI, new String[]{Photo.FLICKR_ID},
                    null, null, null);
            List<Long> localFlickrIdList = getFlickrIdList(cursor);
            List<Long> removeFromDbIdList = new ArrayList<>();
            removeFromDbIdList.addAll(localFlickrIdList);

            int page = 1;
            JsonObject photosJson = getFlickrService().getUserPhotos(page)
                    .getAsJsonObject(getString(R.string.json_key_photos));
            Photos photos = new GsonBuilder().create().fromJson(photosJson, Photos.class);
            photoList.addAll(photos.getPhotoList());
            while (photos.getPage() < photos.getPagesCount()) {
                page++;
                photosJson = getFlickrService().getUserPhotos(page)
                        .getAsJsonObject(getString(R.string.json_key_photos));
                photos = new GsonBuilder().create().fromJson(photosJson, Photos.class);
                photoList.addAll(photos.getPhotoList());
            }

            List<Long> remoteFlickrIdList = getFlickrIdList(photoList);
            List<Long> addIntoDbIdList = new ArrayList<>();
            addIntoDbIdList.addAll(remoteFlickrIdList);

            removeFromDbIdList.removeAll(remoteFlickrIdList);
            addIntoDbIdList.removeAll(localFlickrIdList);

            clearDb(removeFromDbIdList);
            fillDb(photoList, addIntoDbIdList);
            EventBus.getDefault().post(new FetchCompletedEvent(true));
        } else {
            Timber.e("User is null!");
            EventBus.getDefault().post(new FetchCompletedEvent(false));
        }
    }

    private void fillDb(List<Photo> photoList, List<Long> idsToAddIntoDb) {
        Observable.from(photoList)
                .filter(photo -> idsToAddIntoDb.contains(photo.getFlickrId()))
                .flatMap(photo -> {
                    JsonObject photoInfoJson;
                    try {
                        photoInfoJson = getFlickrService().getPhotoInfo(photo)
                                .getAsJsonObject(getString(R.string.json_key_photo));
                    } catch (FlickrException e) {
                        return Observable.error(e);
                    }
                    photo = parsePhotoInfo(photo, photoInfoJson);
                    JsonObject photoSizesJson;
                    try {
                        photoSizesJson = getFlickrService().getPhotoSizes(photo)
                                .getAsJsonObject(getString(R.string.json_key_sizes));
                    } catch (FlickrException e) {
                        return Observable.error(e);
                    }
                    photo = parsePhotoSize(photo, photoSizesJson);
                    return Observable.just(getInsertPhotoOperation(photo));
                })
                .toList()
                .filter(providerOperations -> !providerOperations.isEmpty())
                .subscribe(contentProviderOperations -> {
                    ContentResolver resolver = getApplicationContext().getContentResolver();
                    try {
                        resolver.applyBatch(AUTHORITY, new ArrayList<>(contentProviderOperations));
                    } catch (RemoteException | OperationApplicationException e) {
                        Timber.e("Error with insert photos to DB: %s", e.getMessage());
                        e.printStackTrace();
                    }
                }, throwable -> {
                    Timber.e("Flickr exception: %s", throwable.getMessage());
                    shouldReRunOnThrowable(throwable, getCurrentRunCount(), getRetryLimit());
                });
    }

    private void clearDb(List<Long> idsToRemoveFromDB) {
        Observable.from(idsToRemoveFromDB)
                .map(id -> ContentProviderOperation.newDelete(PHOTOS_CONTENT_URI)
                        .withSelection(Photo.FLICKR_ID + "=?", new String[]{String.valueOf(id)})
                        .build())
                .flatMap(Observable::just)
                .toList()
                .filter(operationList -> !operationList.isEmpty())
                .subscribe(operationList -> {
                    ContentResolver resolver = getApplicationContext().getContentResolver();
                    try {
                        resolver.applyBatch(AUTHORITY, new ArrayList<>(operationList));
                    } catch (RemoteException | OperationApplicationException e) {
                        Timber.e("DB's Error : %s", e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private List<Long> getFlickrIdList(Cursor photoCursor) {
        List<Long> idList = new ArrayList<>();
        if (photoCursor != null && photoCursor.moveToFirst()) {
            Observable.create(new Observable.OnSubscribe<Long>() {
                @Override
                public void call(Subscriber<? super Long> subscriber) {
                    do {
                        subscriber.onNext(photoCursor.getLong(photoCursor.getColumnIndex(Photo.FLICKR_ID)));
                    } while (photoCursor.moveToNext());
                    subscriber.onCompleted();
                }
            }).subscribe(
                    idList::add,
                    throwable -> {
                        Timber.e("Cursor error: %s", throwable.getMessage());
                        throwable.printStackTrace();
                    },
                    () -> {
                        if (!photoCursor.isClosed()) {
                            photoCursor.close();
                        }
                    });

        }
        return idList;
    }

    private List<Long> getFlickrIdList(List<Photo> photoList) {
        List<Long> idList = new ArrayList<>();
        Observable.from(photoList).map(Photo::getFlickrId).subscribe(idList::add);
        return idList;
    }

    private ContentProviderOperation getInsertPhotoOperation(Photo photo) {
        ContentValues values = new Photo.ContentBuilder()
                .flickrId(photo.getFlickrId())
                .owner(photo.getOwner())
                .secret(photo.getSecret())
                .server(photo.getServerId())
                .farm(photo.getFarmId())
                .title(photo.getTitle())
                .isPublic(photo.getIsPublic())
                .isFriend(photo.getIsFriend())
                .isFamily(photo.getIsFamily())
                .rotation(photo.getRotation())
                .originalSecret(photo.getOriginalSecret())
                .originalFormat(photo.getOriginalFormat())
                .takenDate(photo.getTakenDate())
                .lastUpdate(photo.getLastUpdate())
                .width(photo.getWidth())
                .height(photo.getHeight())
                .build();
        return ContentProviderOperation
                .newInsert(PHOTOS_CONTENT_URI).withValues(values).build();
    }

    private Photo parsePhotoSize(Photo photo, JsonObject photoSizesJson) {
        Observable.from(photoSizesJson.getAsJsonArray(getString(R.string.json_key_size)))
                .filter(jsonElement -> {
                    String label = jsonElement.getAsJsonObject()
                            .get(getString(R.string.json_key_size_label)).getAsString();
                    return label.equals(getString(R.string.json_value_size_original));
                })
                .subscribe(jsonElement -> {
                    JsonObject originalSize = jsonElement.getAsJsonObject();
                    photo.setWidth(originalSize.get(getString(R.string.json_key_size_width)).getAsInt());
                    photo.setHeight(originalSize.get(getString(R.string.json_key_size_height)).getAsInt());
                });
        return photo;
    }

    private Photo parsePhotoInfo(Photo photo, JsonObject photoInfoJson) {
        int rotation = photoInfoJson.get(getString(R.string.json_key_rotation)).getAsInt();
        String originalSecret = photoInfoJson.get(getString(R.string.json_key_original_secret))
                .getAsString();
        String originalFormat = photoInfoJson.get(getString(R.string.json_key_original_format))
                .getAsString();
        String takenDate = photoInfoJson.getAsJsonObject(getString(R.string.json_key_dates))
                .get(getString(R.string.json_key_taken_date))
                .getAsString();
        long takenDateInMillis = parseTakenDate(takenDate);
        long lastUpdate = photoInfoJson.getAsJsonObject(getString(R.string.json_key_dates))
                .get(getString(R.string.json_key_last_update))
                .getAsLong() * MILLIS;
        photo.setRotation(rotation);
        photo.setOriginalSecret(originalSecret);
        photo.setOriginalFormat(originalFormat);
        photo.setTakenDate(takenDateInMillis);
        photo.setLastUpdate(lastUpdate);
        return photo;
    }

    private long parseTakenDate(String takenDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.flickr_time_format),
                Locale.getDefault());
        long dateInMillis = 0;
        try {
            dateInMillis = dateFormat.parse(takenDate).getTime();
        } catch (ParseException e) {
            Timber.e("Parsing error: %s", e.getMessage());
            e.printStackTrace();
        }
        return dateInMillis;
    }

    @Override
    protected void onCancel() {
        EventBus.getDefault().post(new FetchCompletedEvent(false));
    }

}
