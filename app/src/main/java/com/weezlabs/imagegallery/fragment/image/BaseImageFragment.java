package com.weezlabs.imagegallery.fragment.image;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.weezlabs.imagegallery.db.FlickrContentProvider;
import com.weezlabs.imagegallery.model.flickr.Photo;


public abstract class BaseImageFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int IMAGES_LOADER = 335;
    public static final long INCORRECT_ID = -1;

    public static final String BUCKET_ID = "com.weezlabs.imagegallery.extra.BUCKET_ID";

    protected CursorAdapter mImageAdapter;
    protected AbsListView mListView;

    private void loadCursor(int loaderId, Bundle args) {
        Loader<Cursor> loader = getLoaderManager().getLoader(loaderId);
        if (loader == null) {
            loader = getLoaderManager().initLoader(loaderId, args, this);
        } else {
            loader = getLoaderManager().restartLoader(loaderId, args, this);
        }
        loader.forceLoad();
    }

    private void loadImagesCursor(long bucketId) {
        Bundle args = new Bundle();
        args.putLong(BUCKET_ID, bucketId);
        loadCursor(IMAGES_LOADER, args);
    }

    protected void loadImages() {
        Bundle args = getArguments();
        if (args != null) {
            long bucketId = args.getLong(BUCKET_ID, INCORRECT_ID);
            if (bucketId != INCORRECT_ID) {
                loadImagesCursor(bucketId);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        Loader<Cursor> imagesLoader = getLoaderManager().getLoader(IMAGES_LOADER);
        if (imagesLoader != null) {
            imagesLoader.reset();
        }
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long bucketId = INCORRECT_ID;
        if (args != null) {
            bucketId = args.getLong(BUCKET_ID, INCORRECT_ID);
        }
        switch (id) {
            case IMAGES_LOADER:
                if (bucketId == Photo.FLICKR_BUCKET_ID) {
                    return new CursorLoader(getActivity(),
                            FlickrContentProvider.PHOTOS_CONTENT_URI,
                            null, null, null, null);
                }
                return bucketId != INCORRECT_ID ?
                        new CursorLoader(getActivity(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Images.Media.BUCKET_ID + "=?",
                                new String[]{String.valueOf(bucketId)},
                                MediaStore.Images.Media.DATE_ADDED + " DESC")
                        : null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == IMAGES_LOADER) {
            mImageAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == IMAGES_LOADER) {
            mImageAdapter.changeCursor(null);
        }
    }

    protected abstract Intent getPreviewIntent(AdapterView<?> parent, View view, int pos, long id);

    protected class OnImageItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = getPreviewIntent(parent, view, position, id);
            if (intent != null) {
                startActivity(intent);
            }
        }
    }
}
