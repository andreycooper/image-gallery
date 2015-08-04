package com.weezlabs.imagegallery.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.fragment.folder.BaseFolderFragment;
import com.weezlabs.imagegallery.fragment.folder.FolderListFragment;
import com.weezlabs.imagegallery.fragment.image.ImageGridFragment;
import com.weezlabs.imagegallery.fragment.image.ImageListFragment;
import com.weezlabs.imagegallery.fragment.image.ImageStaggeredFragment;
import com.weezlabs.imagegallery.model.local.Bucket;

public class FolderDetailActivity extends BaseActivity {
    private Bucket mBucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);

        Bundle extra = getIntent().getExtras();
        mBucket = extra.getParcelable(BaseFolderFragment.EXTRA_BUCKET);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(mBucket.getBucketName());
        }

        ViewMode viewMode = mViewModeStorage.getViewMode();
        setupModeFragment(viewMode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_folder_detail, menu);
        MenuItem item = menu.findItem(R.id.action_change_mode);
        setupModeIcon(item, mViewModeStorage.getViewMode());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_change_mode:
                swapViewMode(item);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void setupModeFragment(ViewMode viewMode) {
        Fragment fragment;
        switch (viewMode) {
            case LIST_MODE:
                fragment = ImageListFragment.newInstance(mBucket.getBucketId());
                break;
            case GRID_MODE:
                fragment = ImageGridFragment.newInstance(mBucket.getBucketId());
                break;
            case STAGGERED_MODE:
                fragment = ImageStaggeredFragment.newInstance(mBucket.getBucketId());
                break;
            default:
                fragment = FolderListFragment.newInstance();
                break;
        }
        replaceFragment(fragment);
    }

}
