package com.weezlabs.imagegallery.fragment.image;

import android.app.FragmentManager;
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

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.fragment.BackHandledFragment;


public abstract class BaseImageFragment extends BackHandledFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int IMAGES_LOADER = 335;
    public static final long INCORRECT_ID = -1;

    public static final String FOLDER_ID = "com.weezlabs.imagegallery.extra.FOLDER_ID";

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

    private void loadImagesCursor(long folderId) {
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, folderId);
        loadCursor(IMAGES_LOADER, args);
    }

    protected void loadImages() {
        Bundle args = getArguments();
        if (args != null) {
            long bucketId = args.getLong(FOLDER_ID, INCORRECT_ID);
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
    public boolean onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
        mBackHandler.setTitle(getString(R.string.app_name));
        mBackHandler.setHamburgerIcon();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long folderId = INCORRECT_ID;
        if (args != null) {
            folderId = args.getLong(FOLDER_ID, INCORRECT_ID);
        }
        if (id == IMAGES_LOADER) {
            return folderId != INCORRECT_ID ?
                    new CursorLoader(getActivity(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null, MediaStore.Images.Media.BUCKET_ID + "=?",
                            new String[]{String.valueOf(folderId)},
                            MediaStore.Images.Media.DATE_ADDED + " DESC")
                    : null;
        }
        return null;
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
