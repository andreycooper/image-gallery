package com.weezlabs.imagegallery.fragment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.PreviewActivity;
import com.weezlabs.imagegallery.adapter.FolderAdapter;
import com.weezlabs.imagegallery.adapter.ImageAdapter;
import com.weezlabs.imagegallery.model.Bucket;
import com.weezlabs.imagegallery.model.Image;


public abstract class BaseFragment extends BackHandledFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int FOLDERS_LOADER = 113;
    public static final int IMAGES_LOADER = 335;
    public static final long INCORRECT_ID = -1;

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
        Loader<Cursor> foldersLoader = getLoaderManager().getLoader(FOLDERS_LOADER);
        if (foldersLoader != null) {
            foldersLoader.reset();
        }
        Loader<Cursor> imagesLoader = getLoaderManager().getLoader(IMAGES_LOADER);
        if (imagesLoader != null) {
            imagesLoader.reset();
        }
    }

    protected void loadFoldersCursor() {
        loadCursor(FOLDERS_LOADER, null);
    }

    protected void loadImagesCursor(long folderId) {
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, folderId);
        loadCursor(IMAGES_LOADER, args);
    }

    protected void loadCursor(int loaderId, Bundle args) {
        Loader<Cursor> loader = getLoaderManager().getLoader(loaderId);
        if (loader == null) {
            loader = getLoaderManager().initLoader(loaderId, args, this);
        } else {
            loader = getLoaderManager().restartLoader(loaderId, args, this);
        }
        loader.forceLoad();
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
            mImageAdapter.changeCursor(null);
            mBackHandlerInterface.setTitle(getString(R.string.app_name));
            mBackHandlerInterface.setHamburgerIcon();
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
                mFolderAdapter.changeCursor(null);
                mBackHandlerInterface.setTitle(bucket.getBucketName());
                mBackHandlerInterface.setBackArrow();
            } else if (mListView.getAdapter() instanceof ImageAdapter) {
                Log.i(LOG_TAG, "click in ImageAdapter, position: " + position + " id: " + id);

                Image image = mImageAdapter.getImage(position);
                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra(PreviewActivity.EXTRA_IMAGE_POSITION, position);
                intent.putExtra(PreviewActivity.EXTRA_BUCKET_ID, image.getBucketId());
                getActivity().startActivity(intent);
            }
        }
    }
}
