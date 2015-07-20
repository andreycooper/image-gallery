package com.weezlabs.imagegallery.storage;

import android.content.SharedPreferences;

import javax.inject.Inject;

import static com.weezlabs.imagegallery.activity.BaseActivity.ViewMode;


public final class ViewModeStorage {

    public static final int DEFAULT_VIEW_MODE = ViewMode.LIST_MODE.getMode();
    private static final String VIEW_MODE = "view_mode";

    private SharedPreferences mPrefs;

    @Inject
    public ViewModeStorage(SharedPreferences prefs) {
        mPrefs = prefs;
    }

    public void setViewMode(ViewMode viewMode) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(VIEW_MODE, viewMode.getMode());
        editor.apply();
    }

    public ViewMode getViewMode() {
        int mode = mPrefs.getInt(VIEW_MODE, DEFAULT_VIEW_MODE);
        switch (mode) {
            case 0:
                return ViewMode.LIST_MODE;
            case 1:
                return ViewMode.GRID_MODE;
            case 2:
                return ViewMode.STAGGERED_MODE;
            default:
                return ViewMode.LIST_MODE;
        }
    }

}
