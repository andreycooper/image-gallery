package com.weezlabs.imagegallery;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.leakcanary.LeakCanary;
import com.weezlabs.imagegallery.dagger.AppComponent;
import com.weezlabs.imagegallery.dagger.DaggerAppComponent;
import com.weezlabs.imagegallery.dagger.module.NetworkModule;
import com.weezlabs.imagegallery.dagger.module.ServiceModule;
import com.weezlabs.imagegallery.dagger.module.StorageModule;

import timber.log.Timber;


public class ImageGalleryApp extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mAppComponent = DaggerAppComponent.builder()
                .storageModule(new StorageModule(this))
                .networkModule(new NetworkModule())
                .serviceModule(new ServiceModule())
                .build();

        LeakCanary.install(this);

        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);

        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }
        });
    }

    public static ImageGalleryApp get(Context context) {
        return (ImageGalleryApp) context.getApplicationContext();
    }

    @NonNull
    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
