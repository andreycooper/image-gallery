package com.weezlabs.imagegallery;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ImageFileFilter implements FileFilter {
    private static final String LOG_TAG = ImageFileFilter.class.getSimpleName();

    public static final String IMAGE_TYPE = "image/";
    public static final String UTF_8 = "UTF-8";

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        }
        String mimeType = getMimeType(pathname);
        return mimeType != null && mimeType.startsWith(IMAGE_TYPE);
    }

    private String getMimeType(File file) {
        String type = null;
        String encoded;
        try {
            encoded = URLEncoder.encode(file.getAbsolutePath().toLowerCase(), UTF_8).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = file.getAbsolutePath().toLowerCase();
        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(encoded.toLowerCase());
        if (extension != null) {
            MimeTypeMap map = MimeTypeMap.getSingleton();
            type = map.getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
