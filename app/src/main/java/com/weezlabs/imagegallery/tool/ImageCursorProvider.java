package com.weezlabs.imagegallery.tool;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.weezlabs.imagegallery.db.FlickrContentProvider;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.local.Bucket;

import java.lang.ref.WeakReference;


public class ImageCursorProvider implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String BUCKET_ID = "com.weezlabs.imagegallery.BUCKET_ID";
    private static final int IMAGES_LOADER = 335;
    public static final int INCORRECT_ID = -1;

    private final WeakReference<ImageCursorReceiver> mReceiverWeakReference;
    private final WeakReference<LoaderManagerProvider> mProviderWeakReference;

    public ImageCursorProvider(LoaderManagerProvider managerProvider, ImageCursorReceiver receiver) {
        mProviderWeakReference = new WeakReference<>(managerProvider);
        mReceiverWeakReference = new WeakReference<>(receiver);
    }

    public void loadImagesCursor(long bucketId) {
        Bundle args = new Bundle();
        args.putLong(BUCKET_ID, bucketId);
        loadCursor(IMAGES_LOADER, args);
    }

    public void onDestroy() {
        Loader<Cursor> imagesLoader = mProviderWeakReference.get()
                .provideLoaderManager().getLoader(IMAGES_LOADER);
        if (imagesLoader != null) {
            imagesLoader.reset();
        }
    }

    private void loadCursor(int loaderId, Bundle args) {
        LoaderManagerProvider managerProvider = mProviderWeakReference.get();
        Loader<Cursor> loader = managerProvider.provideLoaderManager().getLoader(loaderId);
        if (loader == null) {
            loader = managerProvider.provideLoaderManager().initLoader(loaderId, args, this);
        } else {
            loader = managerProvider.provideLoaderManager().restartLoader(loaderId, args, this);
        }
        loader.forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long bucketId = INCORRECT_ID;
        if (args != null) {
            bucketId = args.getLong(BUCKET_ID, INCORRECT_ID);
        }
        switch (id) {
            case IMAGES_LOADER:
                if (bucketId == Bucket.FLICKR_BUCKET_ID) {
                    return new CursorLoader(mProviderWeakReference.get().provideContext(),
                            FlickrContentProvider.PHOTOS_CONTENT_URI,
                            null, null, null, Photo.TAKEN_DATE + " DESC");
                }
                return bucketId != INCORRECT_ID ?
                        new CursorLoader(mProviderWeakReference.get().provideContext(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Images.Media.BUCKET_ID + "=?",
                                new String[]{String.valueOf(bucketId)},
                                MediaStore.Images.Media.DATE_ADDED + " DESC") :
                        null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case IMAGES_LOADER:
                mReceiverWeakReference.get().receiveImageCursor(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case IMAGES_LOADER:
                mReceiverWeakReference.get().receiveImageCursor(null);
                break;
            default:
                break;
        }
    }
}
