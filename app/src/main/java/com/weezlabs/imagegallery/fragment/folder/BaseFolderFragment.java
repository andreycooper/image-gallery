package com.weezlabs.imagegallery.fragment.folder;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.AdapterView.OnItemClickListener;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.adapter.FolderAdapter;
import com.weezlabs.imagegallery.fragment.BackHandledFragment;
import com.weezlabs.imagegallery.model.local.Bucket;


public abstract class BaseFolderFragment extends BackHandledFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = OnFolderItemClickListener.class.getSimpleName();

    public static final int FOLDERS_LOADER = 113;

    protected FolderAdapter mFolderAdapter;
    protected AbsListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        loadFoldersCursor();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Loader<Cursor> foldersLoader = getLoaderManager().getLoader(FOLDERS_LOADER);
        if (foldersLoader != null) {
            foldersLoader.reset();
        }
    }

    protected void loadFoldersCursor() {
        loadCursor(FOLDERS_LOADER, null);
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
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FOLDERS_LOADER) {
            return new CursorLoader(getActivity(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.Media.BUCKET_ID},
                    "1=1) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID,
                    null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == FOLDERS_LOADER) {
            mFolderAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == FOLDERS_LOADER) {
            mFolderAdapter.changeCursor(null);
        }
    }

    protected abstract Fragment getImageFragment(long bucketId);

    protected class OnFolderItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(LOG_TAG, "click in FolderAdapter, position: " + position + " id: " + id);

            Bucket bucket = mFolderAdapter.getBucket(position);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, getImageFragment(bucket.getBucketId()));
            transaction.addToBackStack(null);
            transaction.commit();

            mBackHandler.setTitle(bucket.getBucketName());
            mBackHandler.setBackArrow();
        }
    }

}
