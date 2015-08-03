package com.weezlabs.imagegallery.dagger.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.weezlabs.imagegallery.dagger.scope.PerActivity;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.storage.ViewModeStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    private Context mAppContext;

    public StorageModule(android.content.Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    public Context provideAppContext() {
        return mAppContext;
    }

    @Provides
    @Singleton
    public SharedPreferences providePreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(mAppContext);
    }

    @Provides
    @Singleton
    public ViewModeStorage provideViewModeStorage(SharedPreferences preferences) {
        return new ViewModeStorage(preferences);
    }

    @Provides
    @Singleton
    public FlickrStorage provideFlickrStorage(SharedPreferences preferences) {
        return new FlickrStorage(preferences);
    }
}
