package com.weezlabs.imagegallery.fragment.image;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.tool.ImageCursorProvider;
import com.weezlabs.imagegallery.tool.ImageCursorReceiver;
import com.weezlabs.imagegallery.tool.LoaderManagerProvider;


public abstract class BaseImageFragment extends Fragment
        implements ImageCursorReceiver,
        LoaderManagerProvider {

    public static final long INCORRECT_ID = -1;

    public static final String BUCKET_ID = "com.weezlabs.imagegallery.extra.BUCKET_ID";

    protected CursorAdapter mImageAdapter;
    protected AbsListView mListView;

    private ImageCursorProvider mImageCursorProvider;

    protected void loadImages() {
        Bundle args = getArguments();
        if (args != null) {
            long bucketId = args.getLong(BUCKET_ID, INCORRECT_ID);
            if (bucketId != INCORRECT_ID) {
                mImageCursorProvider.loadImagesCursor(bucketId);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mImageCursorProvider = new ImageCursorProvider(this, this);
    }

    @Override
    public void onDetach() {
        mImageCursorProvider.onDestroy();
        super.onDetach();
    }

    @Override
    public void receiveImageCursor(Cursor cursor) {
        mImageAdapter.changeCursor(cursor);
    }

    @Override
    public LoaderManager provideLoaderManager() {
        return getLoaderManager();
    }

    @Override
    public Context provideContext() {
        return getActivity();
    }

    protected abstract Intent getPreviewIntent(AdapterView<?> parent, View view, int pos, long id);

    protected class OnImageItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = getPreviewIntent(parent, view, position, id);
            if (intent != null) {
                Glide.get(getActivity().getApplicationContext()).clearMemory();
                startActivity(intent);
            }
        }
    }
}
