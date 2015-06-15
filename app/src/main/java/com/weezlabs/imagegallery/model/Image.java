package com.weezlabs.imagegallery.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.format.Formatter;

import com.weezlabs.imagegallery.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Andrey Bondarenko on 15.06.15.
 */
public class Image {

    private long mId;
    private long mTakenDate;
    private long mSize;
    private String mDisplayName;
    private String mPath;
    private int mOrientation;
    private long mBucketId;
    private String mBucketName;

    public Image(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        mTakenDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
        mSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
        mDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        mPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        mOrientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
        mBucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        mBucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
    }

    public long getId() {
        return mId;
    }

    public long getTakenDate() {
        return mTakenDate;
    }

    public String getTakenDate(Context context) {
        return getDate(context, getTakenDate(), context.getString(R.string.format_image_date));
    }

    public long getSize() {
        return mSize;
    }

    public String getSize(Context context) {
        return Formatter.formatShortFileSize(context, getSize());
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getPath() {
        return mPath;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public long getBucketId() {
        return mBucketId;
    }

    public String getBucketName() {
        return mBucketName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Image{");
        sb.append("mId=").append(mId);
        sb.append(", mTakenDate=").append(mTakenDate);
        sb.append(", mSize=").append(mSize);
        sb.append(", mDisplayName='").append(mDisplayName).append('\'');
        sb.append(", mPath='").append(mPath).append('\'');
        sb.append(", mOrientation=").append(mOrientation);
        sb.append(", mBucketId=").append(mBucketId);
        sb.append(", mBucketName='").append(mBucketName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private String getDate(Context context, long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat,
                context.getResources().getConfiguration().locale);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
