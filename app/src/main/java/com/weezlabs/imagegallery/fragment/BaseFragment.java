package com.weezlabs.imagegallery.fragment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.weezlabs.imagegallery.FolderAdapter;
import com.weezlabs.imagegallery.ImageAdapter;
import com.weezlabs.imagegallery.model.Bucket;

import static com.weezlabs.imagegallery.db.ImageContentProvider.INCORRECT_ID;


public abstract class BaseFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int FOLDERS_LOADER = 113;
    public static final int IMAGES_LOADER = 335;

    public static final String FOLDER_ID = "com.weezlabs.imagegallery.extra.FOLDER_ID";

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();
    protected FolderAdapter mFolderAdapter;
    protected ImageAdapter mImageAdapter;
    protected AbsListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFoldersCursor();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Loader<Cursor> folderLoader = getLoaderManager().getLoader(FOLDERS_LOADER);
        if (folderLoader != null) {
            folderLoader.reset();
        }
    }

    protected void loadFoldersCursor() {
        Loader<Cursor> folderLoader = getLoaderManager().getLoader(FOLDERS_LOADER);
        if (folderLoader == null) {
            folderLoader = getLoaderManager().initLoader(FOLDERS_LOADER, null, this);
        } else {
            folderLoader = getLoaderManager().restartLoader(FOLDERS_LOADER, null, this);
        }
        folderLoader.forceLoad();
    }

    protected void loadImagesCursor(long folderId) {
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, folderId);
        Loader<Cursor> imagesLoader = getLoaderManager().getLoader(IMAGES_LOADER);
        if (imagesLoader == null) {
            imagesLoader = getLoaderManager().initLoader(IMAGES_LOADER, args, this);
        } else {
            imagesLoader = getLoaderManager().restartLoader(IMAGES_LOADER, args, this);
        }
        imagesLoader.forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long folderId = INCORRECT_ID;
        if (args != null) {
            folderId = args.getLong(FOLDER_ID, INCORRECT_ID);
        }
        switch (id) {
            case FOLDERS_LOADER:
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                                MediaStore.Images.Media.BUCKET_ID},
                        "1=1) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID,
                        null, null);
            case IMAGES_LOADER:
                return folderId != INCORRECT_ID ?
                        new CursorLoader(getActivity(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Images.Media.BUCKET_ID + "=?",
                                new String[]{String.valueOf(folderId)}, null) : null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FOLDERS_LOADER:
                mFolderAdapter.changeCursor(cursor);
                break;
            case IMAGES_LOADER:
                mImageAdapter.changeCursor(cursor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FOLDERS_LOADER:
                mFolderAdapter.changeCursor(null);
                break;
            case IMAGES_LOADER:
                mImageAdapter.changeCursor(null);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mListView.getAdapter() instanceof FolderAdapter) {
            return false;
        } else if (mListView.getAdapter() instanceof ImageAdapter) {
            loadFoldersCursor();
            mListView.setAdapter(mFolderAdapter);
            mBackHandlerInterface.setHamurgerIcon();
            return true;
        }
        return false;
    }

    protected class OnGalleryItemClickListener implements AdapterView.OnItemClickListener {
        private final String LOG_TAG = ListFragment.OnGalleryItemClickListener.class.getSimpleName();

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListView.getAdapter() instanceof FolderAdapter) {
                Log.i(LOG_TAG, "click in FolderAdapter, position: " + position + " id: " + id);

                Bucket bucket = mFolderAdapter.getBucket(position);
                loadImagesCursor(bucket.getBucketId());
                mListView.setAdapter(mImageAdapter);
                mBackHandlerInterface.setBackArrow();
            } else if (mListView.getAdapter() instanceof ImageAdapter) {
                Log.i(LOG_TAG, "click in ImageAdapter, position: " + position + " id: " + id);
                Toast.makeText(getActivity(), "click in ImageAdapter, position: "
                        + position + " id: " + id, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
