package com.weezlabs.imagegallery.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.controller.PreviewToolbarController;
import com.weezlabs.imagegallery.tool.Events.ChangeTitleEvent;
import com.weezlabs.imagegallery.tool.Events.LoadThumbnailEvent;
import com.weezlabs.imagegallery.tool.Events.ToolbarVisibilityEvent;
import com.weezlabs.imagegallery.tool.ImageCursorProvider;
import com.weezlabs.imagegallery.tool.ImageCursorReceiver;
import com.weezlabs.imagegallery.tool.LoaderManagerProvider;
import com.weezlabs.imagegallery.view.adapter.ImagePagerAdapter;

import de.greenrobot.event.EventBus;


public class PreviewActivity extends AppCompatActivity implements ImageCursorReceiver,
        LoaderManagerProvider {
    public static final String EXTRA_IMAGE_POSITION = "com.weezlabs.imagegallery.extra.IMAGE_POSITION";
    public static final String EXTRA_BUCKET_ID = "com.weezlabs.imagegallery.extra.BUCKET_ID";

    public static final int INCORRECT_ID = -1;

    private boolean mIsFullscreen = false;

    private int mImagePosition = 0;
    private PreviewToolbarController mToolbarController;
    private ImagePagerAdapter mAdapter;
    private ViewPager mPager;
    private ImageCursorProvider mCursorProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mImagePosition = getIntent().getIntExtra(EXTRA_IMAGE_POSITION, 0);
        long bucketId = getIntent().getLongExtra(EXTRA_BUCKET_ID, INCORRECT_ID);

        mCursorProvider = new ImageCursorProvider(this, this);

        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), null);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mImagePosition, false);

        mToolbarController = new PreviewToolbarController(this);
        mToolbarController.create();

        mCursorProvider.loadImagesCursor(bucketId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_IMAGE_POSITION, mPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        int currentPosition = savedInstanceState.getInt(EXTRA_IMAGE_POSITION, -1);
        if (currentPosition != -1) {
            mImagePosition = currentPosition;
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    @Override
    protected void onDestroy() {
        mToolbarController.destroy();
        mCursorProvider.onDestroy();
        super.onDestroy();
    }

    @Override
    public void receiveImageCursor(Cursor cursor) {
        mAdapter.changeCursor(cursor);
        mPager.setCurrentItem(mImagePosition, false);
    }

    @Override
    public LoaderManager provideLoaderManager() {
        return getLoaderManager();
    }

    @Override
    public Context provideContext() {
        return this;
    }

    // EVENTS

    @SuppressWarnings("unused")
    public void onEvent(ChangeTitleEvent event) {
        mToolbarController.setTitle(event.getTitle());
    }

    @SuppressWarnings("unused")
    public void onEvent(ToolbarVisibilityEvent event) {
        mIsFullscreen = !mIsFullscreen;
        mToolbarController.setFullscreen(mIsFullscreen);
    }

    @SuppressWarnings("unused")
    public void onEvent(LoadThumbnailEvent event) {
        mImagePosition = mPager.getCurrentItem();
    }

}
