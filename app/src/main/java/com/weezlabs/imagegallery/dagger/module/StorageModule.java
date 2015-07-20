package com.weezlabs.imagegallery.dagger.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.weezlabs.imagegallery.dagger.scope.PerActivity;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.storage.ViewModeStorage;

import dagger.Module;
import dagger.Provides;

@Module(includes = {NetworkModule.class})
public class StorageModule {

    private Context mAppContext;

    public StorageModule(android.content.Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Provides
    @PerActivity
    public Context provideAppContext() {
        return mAppContext;
    }

    @Provides
    @PerActivity
    public SharedPreferences providePreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(mAppContext);
    }

    @Provides
    @PerActivity
    public ViewModeStorage provideViewModeStorage(SharedPreferences preferences) {
        return new ViewModeStorage(preferences);
    }

    @Provides
    @PerActivity
    public FlickrStorage provideFlickrStorage(SharedPreferences preferences) {
        return new FlickrStorage(preferences);
    }
}
