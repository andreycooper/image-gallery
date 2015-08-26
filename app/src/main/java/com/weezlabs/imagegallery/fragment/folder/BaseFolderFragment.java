package com.weezlabs.imagegallery.fragment.folder;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.FolderDetailActivity;
import com.weezlabs.imagegallery.model.local.Bucket;
import com.weezlabs.imagegallery.view.adapter.FolderAdapter;


public abstract class BaseFolderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = OnFolderItemClickListener.class.getSimpleName();

    public static final int FOLDERS_LOADER = 113;
    public static final String EXTRA_BUCKET = "com.weezlabs.imagegallery.extra.BUCKET";

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

    protected Intent getFolderDetailIntent(Bucket bucket) {
        Intent intent = new Intent(getActivity(), FolderDetailActivity.class);
        intent.putExtra(EXTRA_BUCKET, bucket);
        return intent;
    }

    protected class OnFolderItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(LOG_TAG, "click in FolderAdapter, position: " + position + " id: " + id);

            Bucket bucket = mFolderAdapter.getBucket(position);
            Intent folderDetailIntent = getFolderDetailIntent(bucket);
            Bundle options = ActivityOptionsCompat
                    .makeCustomAnimation(getActivity(), R.anim.slide_right_in, R.anim.slide_right_out)
                    .toBundle();
            ActivityCompat.startActivity(getActivity(), folderDetailIntent, options);

        }
    }

}
