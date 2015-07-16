package com.weezlabs.imagegallery.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.weezlabs.imagegallery.activity.BaseActivity.ViewMode;


public final class Utils {

    public static final int DEFAULT_VIEW_MODE = ViewMode.LIST_MODE.getMode();
    private static final String VIEW_MODE = "view_mode";
    private static final String IS_VISIBLE_INFO = "is_visible_info";

    public static final String IMAGE_TYPE_GIF = "image/gif";
    private static final String UTF_8 = "UTF-8";

    private Utils() {
        // prevent creation of instance
    }

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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
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
        return !TextUtils.isEmpty(mimeType) && mimeType.startsWith(IMAGE_TYPE_GIF);
    }

    public static boolean isVisibleInfo(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        return prefs.getBoolean(IS_VISIBLE_INFO, false);
    }

    /**
     * method converts {@link InputStream} Object into byte[] array.
     *
     * @param stream the {@link InputStream} Object.
     * @return the byte[] array representation of received {@link InputStream} Object.
     * @throws IOException if an error occurs.
     */
    public static byte[] streamToByteArray(InputStream stream) throws IOException {

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        // read bytes from stream, and store them in buffer
        while ((line = stream.read(buffer)) != -1) {
            // Writes bytes from byte array (buffer) into output stream.
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();
        return os.toByteArray();
    }
}
