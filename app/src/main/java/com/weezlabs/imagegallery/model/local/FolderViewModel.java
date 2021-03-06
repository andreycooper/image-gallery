package com.weezlabs.imagegallery.model.local;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


public class FolderViewModel {

    private static final String LOG_TAG = FolderViewModel.class.getSimpleName();

    public static final int MAX_COUNT_IMAGES = 4;

    private List<LocalImage> mLocalImages;
    private int mImageCount;

    public FolderViewModel(Cursor cursor) {
        mLocalImages = new ArrayList<>();
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                mLocalImages.add(new LocalImage(cursor));
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

    public List<LocalImage> getLocalImages() {
        return mLocalImages;
    }
}
