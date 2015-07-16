package com.weezlabs.imagegallery.model.local;

import android.database.Cursor;
import android.provider.MediaStore;


public class Bucket {

    private long mBucketId;
    private String mBucketName;

    public Bucket(Cursor cursor) {
        mBucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        mBucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
    }

    public long getBucketId() {
        return mBucketId;
    }

    public String getBucketName() {
        return mBucketName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Bucket{");
        sb.append("mBucketId=").append(mBucketId);
        sb.append(", mBucketName='").append(mBucketName).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
