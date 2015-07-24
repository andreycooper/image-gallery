package com.weezlabs.imagegallery.job;


import android.widget.Toast;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.RetryConstraint;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.service.flickr.FlickrException;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import retrofit.RetrofitError;

public abstract class BaseFlickrJob extends Job {

    private FlickrStorage mFlickrStorage;
    private FlickrService mFlickrService;

    protected BaseFlickrJob(Params params, FlickrStorage flickrStorage, FlickrService flickrService) {
        super(params);
        mFlickrStorage = flickrStorage;
        mFlickrService = flickrService;
    }

    protected FlickrStorage getFlickrStorage() {
        return mFlickrStorage;
    }

    protected FlickrService getFlickrService() {
        return mFlickrService;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        if (throwable instanceof RetrofitError || throwable instanceof FlickrException) {
            Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.toast_error_login, throwable.getMessage()),
                    Toast.LENGTH_SHORT).show();
            return RetryConstraint.CANCEL;
        }
        return super.shouldReRunOnThrowable(throwable, runCount, maxRunCount);
    }

    protected String getString(int stringId) {
        return getApplicationContext().getString(stringId);
    }
}
