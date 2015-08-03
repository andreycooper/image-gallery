package com.weezlabs.imagegallery.job;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.path.android.jobqueue.Params;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.Photos;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.weezlabs.imagegallery.db.FlickrContentProvider.AUTHORITY;
import static com.weezlabs.imagegallery.db.FlickrContentProvider.PHOTOS_CONTENT_URI;
import static com.weezlabs.imagegallery.db.FlickrContentProvider.PHOTOS_DELETE_CONTENT_URI;

public class FetchFlickrPhotosJob extends BaseFlickrJob {
    private static final AtomicInteger sJobCounter = new AtomicInteger(0);
    public static final String GROUP_ID = "fetch-photos";
    public static final String LOG_TAG = "FETCH_PHOTO";
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
            resolver.delete(PHOTOS_DELETE_CONTENT_URI, null, null);

            JsonObject photosJson = getFlickrService().getUserPhotos()
                    .getAsJsonObject(getString(R.string.json_key_photos));
            Photos photos = new GsonBuilder().create().fromJson(photosJson, Photos.class);
            // TODO: check "page" and "pages"!

            ArrayList<ContentProviderOperation> operationList = getInsertPhotoOperationList(photos);
            resolver.applyBatch(AUTHORITY, operationList);

            Log.i(LOG_TAG, "save in db is done");
            List<Photo> photoList = new ArrayList<>();
            for (Photo photo : photos.getPhotoList()) {
                JsonObject photoInfoJson = getFlickrService().getPhotoInfo(photo)
                        .getAsJsonObject(getString(R.string.json_key_photo));
                photo = parsePhotoInfo(photo, photoInfoJson);
                JsonObject photoSizesJson = getFlickrService().getPhotoSizes(photo)
                        .getAsJsonObject(getString(R.string.json_key_sizes));
                photo = parsePhotoSize(photo, photoSizesJson);
                photoList.add(photo);
            }

            operationList = getUpdatePhotoOperationList(photoList);
            resolver.applyBatch(AUTHORITY, operationList);
            Log.i(LOG_TAG, "update in db is done");
        } else {

        }
    }

    private ArrayList<ContentProviderOperation> getInsertPhotoOperationList(Photos photos) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentValues values = new ContentValues();
        Photo.ContentBuilder builder = new Photo.ContentBuilder();
        for (Photo photo : photos.getPhotoList()) {
            values.clear();
            values = builder.clear()
                    .flickrId(photo.getFlickrId())
                    .owner(photo.getOwner())
                    .secret(photo.getSecret())
                    .server(photo.getServerId())
                    .farm(photo.getFarmId())
                    .title(photo.getTitle())
                    .isPublic(photo.getIsPublic())
                    .isFriend(photo.getIsFriend())
                    .isFamily(photo.getIsFamily())
                    .build();
            operations.add(ContentProviderOperation
                    .newInsert(PHOTOS_CONTENT_URI).withValues(values).build());
        }
        return operations;
    }

    private ArrayList<ContentProviderOperation> getUpdatePhotoOperationList(List<Photo> photoList) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentValues values = new ContentValues();
        Photo.ContentBuilder builder = new Photo.ContentBuilder();
        for (Photo photo : photoList) {
            values.clear();
            values = builder.clear()
                    .rotation(photo.getRotation())
                    .originalSecret(photo.getOriginalSecret())
                    .originalFormat(photo.getOriginalFormat())
                    .takenDate(photo.getTakenDate())
                    .lastUpdate(photo.getLastUpdate())
                    .width(photo.getWidth())
                    .height(photo.getHeight())
                    .build();
            operations.add(ContentProviderOperation.
                    newUpdate(PHOTOS_CONTENT_URI)
                    .withSelection(Photo.FLICKR_ID + "=?",
                            new String[]{String.valueOf(photo.getFlickrId())})
                    .withValues(values)
                    .build());
        }
        return operations;
    }

    private Photo parsePhotoSize(Photo photo, JsonObject photoSizesJson) {
        JsonArray jsonArray = photoSizesJson.getAsJsonArray(getString(R.string.json_key_size));
        JsonObject size;
        String label;
        for (int i = jsonArray.size() - 1; i >= 0; i++) {
            size = jsonArray.get(i).getAsJsonObject();
            label = size.get(getString(R.string.json_key_size_label)).getAsString();
            if (label.equals(getString(R.string.json_value_size_original))) {
                photo.setWidth(size.get(getString(R.string.json_key_size_width)).getAsInt());
                photo.setHeight(size.get(getString(R.string.json_key_size_height)).getAsInt());
                break;
            }
        }
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
            e.printStackTrace();
        }
        return dateInMillis;
    }

    @Override
    protected void onCancel() {

    }

}
