package com.weezlabs.imagegallery.service.flickr;

import android.content.Context;
import android.widget.Toast;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.util.FlickrUtils;

import retrofit.Callback;
import retrofit.RetrofitError;


public abstract class FlickrResponse<T> implements Callback<T> {
    public static final int UNAUTHORIZED_CODE = 401;

    private Context mContext;

    public FlickrResponse(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(mContext, mContext.getString(R.string.toast_flickr_failure,
                error.getResponse().getReason()), Toast.LENGTH_SHORT).show();
        if (error.getResponse().getStatus() == UNAUTHORIZED_CODE) {
            FlickrUtils.resetOAuth(mContext);
        }
    }
}
