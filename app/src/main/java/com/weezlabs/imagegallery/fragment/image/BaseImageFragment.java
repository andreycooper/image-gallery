package com.weezlabs.imagegallery.fragment.image;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.FolderDetailActivity;
import com.weezlabs.imagegallery.model.local.Bucket;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.tool.ImageCursorProvider;
import com.weezlabs.imagegallery.tool.ImageCursorReceiver;
import com.weezlabs.imagegallery.tool.LoaderManagerProvider;
import com.weezlabs.imagegallery.util.FileUtils;
import com.weezlabs.imagegallery.util.TextUtils;

import java.io.File;

import de.greenrobot.event.EventBus;
import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public abstract class BaseImageFragment extends Fragment
        implements ImageCursorReceiver, LoaderManagerProvider {

    public static final long INCORRECT_ID = -1;
    public static final int REQUEST_CAMERA_CODE = 423;

    public static final String BUCKET_ID = "com.weezlabs.imagegallery.extra.BUCKET_ID";

    protected CursorAdapter mImageAdapter;
    protected AbsListView mListView;

    private ImageCursorProvider mImageCursorProvider;
    private long mBucketId;
    private Uri mOutputPictureUri;
    private ProgressBar mProgressBar;
    private TextView mEmptyFlickText;

    protected void loadImages() {
        Bundle args = getArguments();
        if (args != null) {
            mBucketId = args.getLong(BUCKET_ID, INCORRECT_ID);
            if (mBucketId != INCORRECT_ID) {
                mImageCursorProvider.loadImagesCursor(mBucketId);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mImageCursorProvider = new ImageCursorProvider(this, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = getRootView(inflater, container);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.fetch_photos_progress);
        setupProgressBar(mProgressBar);

        mEmptyFlickText = (TextView) rootView.findViewById(R.id.empty_flickr_list_text);

        if (mListView != null) {
            mListView.setEmptyView(mProgressBar);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new OnFabClickListener());
        if (mBucketId == Bucket.FLICKR_BUCKET_ID) {
            fab.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void setupProgressBar(ProgressBar progressBar) {
        IndeterminateProgressDrawable progressDrawable = new IndeterminateProgressDrawable(getActivity());
        int progressColor = getResources().getColor(R.color.material_drawer_primary_dark);
        progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(progressDrawable);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBucketId == Bucket.FLICKR_BUCKET_ID && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        if (mBucketId == Bucket.FLICKR_BUCKET_ID && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mImageCursorProvider.onDestroy();
        super.onDestroy();
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
        return getActivity().getApplicationContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == FolderDetailActivity.RESULT_OK &&
                requestCode == REQUEST_CAMERA_CODE) {
            getActivity().sendBroadcast(
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mOutputPictureUri));
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Events.FetchCompletedEvent event) {
        if (event.isSuccess() && mImageAdapter.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setEmptyView(mEmptyFlickText);
            mEmptyFlickText.setVisibility(View.VISIBLE);
        }
    }

    protected abstract Intent getPreviewIntent(AdapterView<?> parent, View view, int pos, long id);

    protected abstract View getRootView(@NonNull LayoutInflater inflater, ViewGroup container);

    protected class OnImageItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view,
                                int position, long id) {
            Intent intent = getPreviewIntent(parent, view, position, id);
            if (intent != null) {
                Glide.get(getActivity().getApplicationContext()).clearMemory();
                Bundle options = ActivityOptionsCompat
                        .makeCustomAnimation(getActivity(), R.anim.push_down_in, R.anim.push_down_out)
                        .toBundle();
                ActivityCompat.startActivity(getActivity(), intent, options);
            }
        }
    }

    private class OnFabClickListener implements View.OnClickListener {

        @Override
        public void onClick(@NonNull View view) {
            String folderPath = mImageCursorProvider.provideFolderPath();
            if (TextUtils.isNonEmpty(folderPath)) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File outputPicture = new File(folderPath,
                            TextUtils.getCurrentDate(getActivity().getApplicationContext()) +
                                    FileUtils.JPG_EXTENSION);
                    mOutputPictureUri = Uri.fromFile(outputPicture);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputPictureUri);
                    startActivityForResult(takePictureIntent,
                            REQUEST_CAMERA_CODE);
                }
            }
        }
    }
}
