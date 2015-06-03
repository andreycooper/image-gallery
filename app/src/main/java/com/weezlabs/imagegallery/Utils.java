package com.weezlabs.imagegallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.weezlabs.imagegallery.fragment.BaseFragment.ViewMode;


public class Utils {

    public static final int DEFAULT_VIEW_MODE = ViewMode.LIST_MODE.getMode();
    private static final String VIEW_MODE = "view_mode";

    public static void setViewMode(Context context, ViewMode viewMode) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext()).edit();
        editor.putInt(VIEW_MODE, viewMode.getMode());
        editor.apply();
    }

    public static ViewMode getViewMode(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        int mode = prefs.getInt(VIEW_MODE, DEFAULT_VIEW_MODE);
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
