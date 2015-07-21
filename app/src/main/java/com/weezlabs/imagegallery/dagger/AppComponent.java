package com.weezlabs.imagegallery.dagger;

import com.weezlabs.imagegallery.activity.BaseActivity;
import com.weezlabs.imagegallery.activity.FlickrLoginActivity;
import com.weezlabs.imagegallery.dagger.module.NetworkModule;
import com.weezlabs.imagegallery.dagger.module.StorageModule;
import com.weezlabs.imagegallery.dagger.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(
        modules = {StorageModule.class, NetworkModule.class}
)
public interface AppComponent {
    void inject(BaseActivity activity);

    void inject(FlickrLoginActivity activity);
}
