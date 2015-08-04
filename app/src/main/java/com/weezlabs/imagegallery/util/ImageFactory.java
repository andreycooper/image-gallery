package com.weezlabs.imagegallery.util;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.local.Bucket;
import com.weezlabs.imagegallery.model.local.LocalImage;

public final class ImageFactory {

    public static final String IMAGE = "com.weezlabs.imagegallery.IMAGE";
    public static final int INCORRECT_INDEX = -1;

    private ImageFactory() {

    }

    public static Image buildImage(Cursor cursor) {
        if (cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID) == INCORRECT_INDEX) {
            return new Photo(cursor);
        } else {
            return new LocalImage(cursor);
        }
    }

    public static String getImageTitle(Cursor cursor) {
        String title = null;
        if (cursor != null && !cursor.isClosed()) {
            if (cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME) == INCORRECT_INDEX) {
                title = cursor.getString(cursor.getColumnIndex(Photo.TITLE));
            } else {
                title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            }
        }
        return title;
    }

    public static Bundle buildFragmentArguments(Image image) {
        Bundle args = new Bundle();
        if (image instanceof LocalImage) {
            args.putParcelable(IMAGE, (LocalImage) image);
        } else if (image instanceof Photo) {
            args.putParcelable(IMAGE, (Photo) image);
        }
        return args;
    }

    public static Bucket buildFlickrBucket(Context context) {
        return new Bucket(Bucket.FLICKR_BUCKET_ID,
                context.getApplicationContext().getString(R.string.flickr_bucket_name));
    }
}
