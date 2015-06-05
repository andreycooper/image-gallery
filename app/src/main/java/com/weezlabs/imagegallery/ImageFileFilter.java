package com.weezlabs.imagegallery;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileFilter;


public class ImageFileFilter implements FileFilter {
    public static final String IMAGE_TYPE = "image/";

    @Override
    public boolean accept(File pathname) {
        String mimeType = getMimeType(pathname);
        return pathname.isDirectory() || (mimeType != null && mimeType.startsWith(IMAGE_TYPE));
    }

    private String getMimeType(File file) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath().toLowerCase());
        if (extension != null) {
            MimeTypeMap map = MimeTypeMap.getSingleton();
            type = map.getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
