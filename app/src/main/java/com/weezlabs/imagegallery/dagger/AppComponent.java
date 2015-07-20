package com.weezlabs.imagegallery.dagger;

import com.weezlabs.imagegallery.activity.BaseActivity;
import com.weezlabs.imagegallery.activity.FlickrLoginActivity;
import com.weezlabs.imagegallery.dagger.module.AppModule;
import com.weezlabs.imagegallery.dagger.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(
        modules = {AppModule.class}
)
public interface AppComponent {
    void inject(BaseActivity activity);

    void inject(FlickrLoginActivity activity);
}
