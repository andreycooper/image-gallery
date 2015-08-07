package com.weezlabs.imagegallery.activity.controller;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.BaseActivity;
import com.weezlabs.imagegallery.activity.BaseActivity.ViewMode;
import com.weezlabs.imagegallery.activity.FlickrLoginActivity;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.storage.ViewModeStorage;
import com.weezlabs.imagegallery.tool.Events.LoadFlickrPhotosEvent;
import com.weezlabs.imagegallery.util.NetworkUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static com.weezlabs.imagegallery.db.FlickrContentProvider.PHOTOS_DELETE_CONTENT_URI;

public final class NavigationDrawerController {
    private static final int CURRENT_ACCOUNT_ID = 10;
    private static final int ADD_ACCOUNT_ID = 20;
    private static final int DELETE_ACCOUNT_ID = 30;
    private static final int FLICKR_PHOTOS_ID = 40;

    private Context mAppContext;
    private ViewModeStorage mViewModeStorage;
    private FlickrStorage mFlickrStorage;
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;

    private NavigationDrawerController() {
    }

    public void setCurrentViewMode() {
        mDrawer.setSelection(getDrawerSelectedMode(), false);
    }

    public Bundle saveInstanceState(Bundle outState) {
        return mDrawer.saveInstanceState(outState);
    }

    public boolean isDrawerOpen() {
        return mDrawer.isDrawerOpen();
    }

    public void closeDrawer() {
        mDrawer.closeDrawer();
    }

    public void refillAccountHeader() {
        mAccountHeader.clear();
        mAccountHeader.setProfiles(getProfileList());
    }

    private void setAppContext(Context appContext) {
        mAppContext = appContext;
    }

    private void setViewModeStorage(ViewModeStorage viewModeStorage) {
        mViewModeStorage = viewModeStorage;
    }

    private void setFlickrStorage(FlickrStorage flickrStorage) {
        mFlickrStorage = flickrStorage;
    }

    private void setAccountHeader(AccountHeader accountHeader) {
        mAccountHeader = accountHeader;
    }

    private void setDrawer(Drawer drawer) {
        mDrawer = drawer;
    }

    private int getDrawerSelectedMode() {
        int viewMode = mViewModeStorage.getViewMode().getMode();
        for (int i = 0; i < mDrawer.getDrawerItems().size(); i++) {
            if (viewMode == mDrawer.getDrawerItems().get(i).getIdentifier()) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<IProfile> getProfileList() {
        ArrayList<IProfile> profiles = new ArrayList<>();
        User user = mFlickrStorage.restoreFlickrUser();
        if (user != null) {
            profiles.addAll(getUserProfile(user));
        } else {
            profiles.add(new ProfileSettingDrawerItem()
                    .withName(getString(R.string.label_account_header_add_account))
                    .withDescription(getString(R.string.label_account_header_add_account_description))
                    .withIcon(new IconicsDrawable(mAppContext, GoogleMaterial.Icon.gmd_add)
                            .actionBar())
                    .withIdentifier(ADD_ACCOUNT_ID));
        }
        return profiles;
    }

    private ArrayList<IProfile> getUserProfile(User user) {
        ArrayList<IProfile> userProfiles = new ArrayList<>();
        if (user != null) {
            userProfiles.add(new ProfileDrawerItem()
                    .withEmail(user.getUsername())
                    .withName(user.getRealName())
                    .withIcon(FlickrStorage.getIconUrl(user))
                    .withIdentifier(CURRENT_ACCOUNT_ID));
            userProfiles.add(new ProfileSettingDrawerItem()
                    .withName(getString(R.string.label_account_header_delete_account))
                    .withIcon(new IconicsDrawable(mAppContext, GoogleMaterial.Icon.gmd_delete)
                            .actionBar())
                    .withIcon(GoogleMaterial.Icon.gmd_settings)
                    .withIdentifier(DELETE_ACCOUNT_ID));
        }
        return userProfiles;
    }

    private String getString(int stringId) {
        return mAppContext.getString(stringId);
    }


    public static final class Builder {
        final NavigationDrawerController mController = new NavigationDrawerController();
        private BaseActivity mActivity;
        private FlickrStorage mFlickrStorage;
        private AccountHeader mAccountHeader;
        private Toolbar mToolbar;
        private Bundle mState;
        private Drawer mDrawer;


        public Builder withActivity(BaseActivity activity) {
            mActivity = activity;
            mController.setAppContext(activity.getApplicationContext());
            return this;
        }

        public Builder withToolbar(Toolbar toolbar) {
            mToolbar = toolbar;
            return this;
        }

        public Builder withStorages(ViewModeStorage viewModeStorage, FlickrStorage flickrStorage) {
            mFlickrStorage = flickrStorage;
            mController.setViewModeStorage(viewModeStorage);
            mController.setFlickrStorage(flickrStorage);
            return this;
        }

        public Builder withState(Bundle state) {
            mState = state;
            return this;
        }

        public NavigationDrawerController build() {
            mAccountHeader = getHeader();
            mDrawer = getDrawer(mAccountHeader);
            mController.setAccountHeader(mAccountHeader);
            mController.setDrawer(mDrawer);
            return mController;
        }

        private AccountHeader getHeader() {
            return new AccountHeaderBuilder()
                    .withActivity(mActivity)
                    .withTranslucentStatusBar(true)
                    .withHeaderBackground(R.drawable.drawer_header_background)
                    .withProfiles(mController.getProfileList())
                    .withSavedInstance(mState)
                    .withOnAccountHeaderListener((view, profile, current) -> {
                        if (profile != null) {
                            switch (profile.getIdentifier()) {
                                case ADD_ACCOUNT_ID:
                                    startFlickrLogin();
                                    break;
                                case CURRENT_ACCOUNT_ID:
                                    EventBus.getDefault().postSticky(new LoadFlickrPhotosEvent());
                                    break;
                                case DELETE_ACCOUNT_ID:
                                    mFlickrStorage.resetOAuth();
                                    mActivity.getContentResolver()
                                            .delete(PHOTOS_DELETE_CONTENT_URI, null, null);
                                    mController.refillAccountHeader();
                                    break;
                                default:
                                    break;
                            }
                        }
                        return false;
                    })
                    .build();
        }

        private Drawer getDrawer(AccountHeader accountHeader) {
            return new DrawerBuilder()
                    .withActivity(mActivity)
                    .withToolbar(mToolbar)
                    .withSavedInstance(mState)
                    .withActionBarDrawerToggleAnimated(true)
                    .withAccountHeader(accountHeader)
                    .addDrawerItems(
                            new SectionDrawerItem()
                                    .withName(R.string.label_drawer_section_view_mode)
                                    .setDivider(false),
                            new PrimaryDrawerItem()
                                    .withName(R.string.label_drawer_list)
                                    .withIcon(GoogleMaterial.Icon.gmd_view_list)
                                    .withIdentifier(ViewMode.LIST_MODE.getMode()),
                            new PrimaryDrawerItem()
                                    .withName(R.string.label_drawer_grid)
                                    .withIcon(GoogleMaterial.Icon.gmd_view_module)
                                    .withIdentifier(ViewMode.GRID_MODE.getMode()),
                            new PrimaryDrawerItem()
                                    .withName(R.string.label_drawer_staggered)
                                    .withIcon(GoogleMaterial.Icon.gmd_view_quilt)
                                    .withIdentifier(ViewMode.STAGGERED_MODE.getMode()),
                            new DividerDrawerItem(),
                            new PrimaryDrawerItem()
                                    .withName(R.string.label_drawer_flickr_photos)
                                    .withIcon(GoogleMaterial.Icon.gmd_monochrome_photos)
                                    .withIdentifier(FLICKR_PHOTOS_ID)
                    )
                    .withOnDrawerNavigationListener(view -> {
                        mActivity.onBackPressed();
                        return true;
                    })
                    .withOnDrawerItemClickListener((adapterView, view, position, l, drawerItem) -> {
                        if (drawerItem != null) {
                            if (isViewModeDrawerItem(drawerItem)) {
                                mActivity.changeViewMode(drawerItem.getIdentifier());
                                mDrawer.closeDrawer();
                            } else if (drawerItem.getIdentifier() == FLICKR_PHOTOS_ID) {
                                EventBus.getDefault().postSticky(new LoadFlickrPhotosEvent());
                                mDrawer.closeDrawer();
                            }
                        }
                        return true;
                    })
                    .build();
        }

        private void startFlickrLogin() {
            if (NetworkUtils.isOnline(mActivity)) {
                if (!mFlickrStorage.isAuthenticated()) {
                    Intent intent = new Intent(mActivity, FlickrLoginActivity.class);
                    mActivity.startActivity(intent);
                }
            } else {
                Snackbar.make(mActivity.getWindow().getDecorView().getRootView(),
                        mActivity.getString(R.string.toast_internet_check),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

        private boolean isViewModeDrawerItem(IDrawerItem drawerItem) {
            return drawerItem.getIdentifier() >= ViewMode.LIST_MODE.getMode()
                    && drawerItem.getIdentifier() <= ViewMode.STAGGERED_MODE.getMode();
        }

    }
}
