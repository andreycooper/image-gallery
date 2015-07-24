package com.weezlabs.imagegallery.job;


import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.path.android.jobqueue.Params;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.Photos;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import java.util.concurrent.atomic.AtomicInteger;

public class FetchFlickrPhotosJob extends BaseFlickrJob {
    private static final AtomicInteger sJobCounter = new AtomicInteger(0);
    public static final String GROUP_ID = "fetch-photos";
    public static final String LOG_TAG = "FETCH_PHOTO";

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
            JsonObject photosJson = getFlickrService().getUserPhotos()
                    .getAsJsonObject(getString(R.string.json_key_photos));
            Photos photos = new GsonBuilder().create().fromJson(photosJson, Photos.class);
            for (Photo photo : photos.getPhotoList()) {
                JsonObject photoInfoJson = getFlickrService().getPhotoInfo(photo)
                        .getAsJsonObject(getString(R.string.json_key_photo));
                photo = parsePhotoInfo(photo, photoInfoJson);
                Log.i(LOG_TAG, photo.toString());
                Log.i(LOG_TAG, FlickrStorage.getPhotoUrl(photo));
                Log.i(LOG_TAG, FlickrStorage.getOriginalPhotoUrl(photo));
            }
        } else {

        }
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
        photo.setRotation(rotation);
        photo.setOriginalSecret(originalSecret);
        photo.setOriginalFormat(originalFormat);
        photo.setTakenDate(takenDate);
        return photo;
    }

    @Override
    protected void onCancel() {

    }

}
