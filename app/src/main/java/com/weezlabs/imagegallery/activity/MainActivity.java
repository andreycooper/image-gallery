package com.weezlabs.imagegallery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.controller.NavigationDrawerController;
import com.weezlabs.imagegallery.fragment.folder.BaseFolderFragment;
import com.weezlabs.imagegallery.job.FetchFlickrPhotosJob;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.tool.Events.UserLogonEvent;
import com.weezlabs.imagegallery.util.ImageFactory;
import com.weezlabs.imagegallery.util.NetworkUtils;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {

    private NavigationDrawerController mDrawerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(toolbar);

        mDrawerController = new NavigationDrawerController.Builder()
                .withActivity(this)
                .withStorages(mViewModeStorage, mFlickrStorage)
                .withToolbar(toolbar)
                .withState(savedInstanceState)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
        // show correct fragment also correct states of drawer and menu
        ViewMode viewMode = mViewModeStorage.getViewMode();
        setupModeFragment(viewMode);
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_change_mode);
            setupModeIcon(item, mViewModeStorage.getViewMode());
        }
        mDrawerController.setCurrentViewMode();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, mMenu);
        MenuItem item = mMenu.findItem(R.id.action_change_mode);
        setupModeIcon(item, mViewModeStorage.getViewMode());
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_change_mode:
                swapViewMode(item);
                mDrawerController.setCurrentViewMode();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFlickrPhotos() {
        if (NetworkUtils.isOnline(this)) {
            User user = mFlickrStorage.restoreFlickrUser();
            if (user != null) {
                Intent intent = new Intent(this, FolderDetailActivity.class);
                intent.putExtra(BaseFolderFragment.EXTRA_BUCKET, ImageFactory.buildFlickrBucket(this));
                startActivity(intent);
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(),
                        getString(R.string.toast_flickr_login_need),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(),
                    getString(R.string.toast_internet_check),
                    Snackbar.LENGTH_SHORT)
                    .show();
            mDrawerController.setCurrentViewMode();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mDrawerController.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerController.isDrawerOpen()) {
            mDrawerController.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UserLogonEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        mDrawerController.refillAccountHeader();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Events.LoadFlickrPhotosEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        loadFlickrPhotos();
    }

}
