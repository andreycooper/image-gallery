package com.weezlabs.imagegallery.model;

import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;


public class FolderViewModel {

    private static final String LOG_TAG = FolderViewModel.class.getSimpleName();

    public static final int MAX_COUNT_IMAGES = 4;

    private List<String> mImagePaths;
    private int mImageCount;

    public FolderViewModel(Cursor cursor) {
        mImagePaths = new ArrayList<>();
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                mImagePaths.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                i++;
            } while (i < MAX_COUNT_IMAGES && cursor.moveToNext());
            mImageCount = cursor.getCount();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public int getImageCount() {
        return mImageCount;
    }

    public List<String> getImagePaths() {
        return mImagePaths;
    }
}
