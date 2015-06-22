package com.weezlabs.imagegallery.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.weezlabs.imagegallery.activity.BaseActivity.ViewMode;


public final class Utils {

    public static final String IMAGE_TYPE_GIF = "image/gif";
    private static final String UTF_8 = "UTF-8";

    private Utils() {
        // prevent creation of instance
    }

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

    public static String getMimeType(String filePath) {
        String type = null;
        String encoded;
        try {
            encoded = URLEncoder.encode(filePath.toLowerCase(), UTF_8).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = filePath.toLowerCase();
        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(encoded.toLowerCase());
        if (extension != null) {
            MimeTypeMap map = MimeTypeMap.getSingleton();
            type = map.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean isGifFile(String filePath) {
        String mimeType = getMimeType(filePath);
        return mimeType != null && mimeType.startsWith(IMAGE_TYPE_GIF);
    }
}
