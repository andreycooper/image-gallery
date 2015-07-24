package com.weezlabs.imagegallery.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public final class FileUtils {
    public static final String IMAGE_TYPE = "image/";
    public static final String IMAGE_TYPE_GIF = IMAGE_TYPE + "gif";
    private static final String IS_VISIBLE_INFO = "is_visible_info";
    private static final String UTF_8 = "UTF-8";

    private FileUtils() {
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
        return !android.text.TextUtils.isEmpty(mimeType) && mimeType.startsWith(IMAGE_TYPE_GIF);
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
