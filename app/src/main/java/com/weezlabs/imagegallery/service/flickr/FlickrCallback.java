package com.weezlabs.imagegallery.service.flickr;

import android.content.Context;
import android.widget.Toast;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import retrofit.Callback;
import retrofit.RetrofitError;


public abstract class FlickrCallback<T> implements Callback<T> {
    public static final int UNAUTHORIZED_CODE = 401;

    protected Context mContext;
    protected FlickrStorage mFlickrStorage;

    public FlickrCallback(Context context, FlickrStorage flickrStorage) {
        mContext = context.getApplicationContext();
        mFlickrStorage = flickrStorage;
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(mContext, mContext.getString(R.string.toast_flickr_failure,
                error.getResponse().getReason()), Toast.LENGTH_SHORT).show();
        if (error.getResponse().getStatus() == UNAUTHORIZED_CODE) {
            mFlickrStorage.resetOAuth();
        }
    }
}
