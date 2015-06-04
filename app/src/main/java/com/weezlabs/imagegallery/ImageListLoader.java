package com.weezlabs.imagegallery;

import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.weezlabs.imagegallery.model.Folder;
import com.weezlabs.imagegallery.model.Image;

import java.io.File;
import java.io.FileFilter;

import static com.weezlabs.imagegallery.db.ImageContentProvider.FOLDERS_CONTENT_URI;
import static com.weezlabs.imagegallery.db.ImageContentProvider.IMAGES_CONTENT_URI;
import static com.weezlabs.imagegallery.db.ImageContentProvider.INCORRECT_ID;


public class ImageListLoader extends AsyncTaskLoader<Void> {
    private static final String LOG_TAG = ImageListLoader.class.getSimpleName();

    public static final String ROOT_FOLDER = Environment.getExternalStorageDirectory().getPath();
    public static final String FOLDER = "com.weezlabs.imagegallery.FOLDER";

    private String mFolder;

    public ImageListLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            mFolder = args.getString(FOLDER);
        }
        if (TextUtils.isEmpty(mFolder)) {
            mFolder = ROOT_FOLDER;
        }
    }

    @Override
    public Void loadInBackground() {
        scanFolder(mFolder);
        return null;
    }

    private void scanFolder(String parentFolder) {
        File folder = new File(parentFolder);
        long folderId = INCORRECT_ID;

        FileFilter fileFilter = new ImageFileFilter();
        File[] files = folder.listFiles(fileFilter);

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file.getName());
            } else {
                if (folderId == INCORRECT_ID) {
                    folderId = storeParentFolderToDb(folder);
                } else {
                    storeImageToDb(file, folderId);
                }
            }
        }
    }

    private long storeParentFolderToDb(File parentFolder) {
        Folder folder = new Folder(parentFolder.getAbsolutePath(), parentFolder.lastModified(), true);
        ContentValues values = new Folder.Builder()
                .path(folder.getPath())
                .date(folder.getDate())
                .local(folder.isLocal()).build();
        Uri resultUri = getContext().getContentResolver()
                .insert(FOLDERS_CONTENT_URI, values);

        return ContentUris.parseId(resultUri);
    }

    private long storeImageToDb(File file, long folderId) {
        Image image = new Image(file.getAbsolutePath(), file.lastModified(), file.length(), true);
        ContentValues values = new Image.Builder()
                .path(image.getPath())
                .date(image.getDate())
                .size(image.getSize())
                .local(image.isLocalFile())
                .folderId(folderId).build();
        Uri resultUri = getContext().getContentResolver()
                .insert(IMAGES_CONTENT_URI, values);

        return ContentUris.parseId(resultUri);
    }


    private static class ImageFileFilter implements FileFilter {
        public static final String IMAGE_TYPE = "image/";

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() || getMimeType(pathname).startsWith(IMAGE_TYPE);
        }

        private String getMimeType(File file) {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            if (extension != null) {
                MimeTypeMap map = MimeTypeMap.getSingleton();
                type = map.getMimeTypeFromExtension(extension);
            }
            return type;
        }
    }
}
