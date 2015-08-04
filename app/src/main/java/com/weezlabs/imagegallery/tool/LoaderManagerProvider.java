package com.weezlabs.imagegallery.tool;


import android.app.LoaderManager;
import android.content.Context;

public interface LoaderManagerProvider {
    LoaderManager provideLoaderManager();

    Context provideContext();
}
