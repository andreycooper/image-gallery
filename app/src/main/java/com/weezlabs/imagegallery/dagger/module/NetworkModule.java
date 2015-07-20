package com.weezlabs.imagegallery.dagger.module;

import android.content.Context;

import com.weezlabs.imagegallery.dagger.scope.PerActivity;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @PerActivity
    @Provides
    public FlickrService provideFlickrService(Context context, FlickrStorage flickrStorage) {
        return new FlickrService(context, flickrStorage);
    }
}
