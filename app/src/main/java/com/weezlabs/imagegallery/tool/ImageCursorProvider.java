package com.weezlabs.imagegallery.tool;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.weezlabs.imagegallery.db.FlickrContentProvider;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.util.FileUtils;

import java.lang.ref.WeakReference;

import static com.weezlabs.imagegallery.model.local.Bucket.FLICKR_BUCKET_ID;


public class ImageCursorProvider implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String BUCKET_ID = "com.weezlabs.imagegallery.BUCKET_ID";
    private static final int IMAGES_LOADER = 335;
    public static final int INCORRECT_ID = -1;

    private final WeakReference<ImageCursorReceiver> mReceiverWeakReference;
    private final WeakReference<LoaderManagerProvider> mProviderWeakReference;
    private String mFolderPath;
    private long mCurrentBucketId;

    public ImageCursorProvider(LoaderManagerProvider managerProvider, ImageCursorReceiver receiver) {
        mProviderWeakReference = new WeakReference<>(managerProvider);
        mReceiverWeakReference = new WeakReference<>(receiver);
    }

    public void loadImagesCursor(long bucketId) {
        mCurrentBucketId = bucketId;
        Bundle args = new Bundle();
        args.putLong(BUCKET_ID, bucketId);
        loadCursor(IMAGES_LOADER, args);
    }

    public void onDestroy() {
        if (mProviderWeakReference.get() != null) {
            Loader<Cursor> imagesLoader = mProviderWeakReference.get()
                    .provideLoaderManager().getLoader(IMAGES_LOADER);
            if (imagesLoader != null) {
                imagesLoader.reset();
            }
        }
        mFolderPath = null;
        mProviderWeakReference.clear();
        mReceiverWeakReference.clear();
    }

    public String provideFolderPath() {
        return mFolderPath;
    }

    private void loadCursor(int loaderId, Bundle args) {
        LoaderManagerProvider managerProvider = mProviderWeakReference.get();
        if (managerProvider != null) {
            Loader<Cursor> loader = managerProvider.provideLoaderManager().getLoader(loaderId);
            if (loader == null) {
                loader = managerProvider.provideLoaderManager().initLoader(loaderId, args, this);
            } else {
                loader = managerProvider.provideLoaderManager().restartLoader(loaderId, args, this);
            }
            loader.forceLoad();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mProviderWeakReference.get() == null) {
            return null;
        }
        long bucketId = INCORRECT_ID;
        if (args != null) {
            bucketId = args.getLong(BUCKET_ID, INCORRECT_ID);
        }
        switch (id) {
            case IMAGES_LOADER:
                if (bucketId == FLICKR_BUCKET_ID) {
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
                if (isCorrectBucket()) {
                    if (data != null && data.moveToFirst()) {
                        mFolderPath = FileUtils.getFolderPath(data.getString(
                                data.getColumnIndex(MediaStore.Images.Media.DATA)));
                    }
                }
                if (mReceiverWeakReference.get() != null) {
                    mReceiverWeakReference.get().receiveImageCursor(data);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case IMAGES_LOADER:
                mFolderPath = null;
                if (mReceiverWeakReference.get() != null) {
                    mReceiverWeakReference.get().receiveImageCursor(null);
                }
                break;
            default:
                break;
        }
    }

    private boolean isCorrectBucket() {
        return mCurrentBucketId != INCORRECT_ID && mCurrentBucketId != FLICKR_BUCKET_ID;
    }
}
