package com.weezlabs.imagegallery.model.local;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.Formatter;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class LocalImage implements Parcelable, Image {

    private int mId;
    private long mTakenDate;
    private long mDateModified;
    private long mSize;
    private String mDisplayName;
    private String mPath;
    private String mMimeType;
    private int mOrientation;
    private int mWidth;
    private int mHeight;
    private long mBucketId;
    private String mBucketName;

    public static String getPath(Cursor cursor) {
        return cursor != null && !cursor.isClosed() ?
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)) :
                null;
    }

    public static String getDisplayName(Cursor cursor) {
        return cursor != null && !cursor.isClosed() ?
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)) :
                null;
    }

    public LocalImage(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        mTakenDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
        mDateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
        mSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
        mDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        mPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        mMimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        mOrientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
        mWidth = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
        mHeight = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
        mBucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        mBucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
    }

    public int getId() {
        return mId;
    }

    public long getTakenDate() {
        return mTakenDate;
    }

    public String getReadableTakenDate(Context context) {
        return getFormatedDate(context, getTakenDate(), context.getString(R.string.format_image_date));
    }

    public long getDateModified() {
        return mDateModified;
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

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String getOriginalPath() {
        return mPath;
    }

    @Override
    public String getMimeType() {
        return mMimeType;
    }

    public int getOrientation() {
        return mOrientation;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    public long getBucketId() {
        return mBucketId;
    }

    public String getBucketName() {
        return mBucketName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocalImage{");
        sb.append("mId=").append(mId);
        sb.append(", mTakenDate=").append(mTakenDate);
        sb.append(", mDateModified=").append(mDateModified);
        sb.append(", mSize=").append(mSize);
        sb.append(", mDisplayName='").append(mDisplayName).append('\'');
        sb.append(", mPath='").append(mPath).append('\'');
        sb.append(", mMimeType='").append(mMimeType).append('\'');
        sb.append(", mOrientation=").append(mOrientation);
        sb.append(", mWidth=").append(mWidth);
        sb.append(", mHeight=").append(mHeight);
        sb.append(", mBucketId=").append(mBucketId);
        sb.append(", mBucketName='").append(mBucketName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private String getFormatedDate(Context context, long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat,
                context.getResources().getConfiguration().locale);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeLong(this.mTakenDate);
        dest.writeLong(this.mDateModified);
        dest.writeLong(this.mSize);
        dest.writeString(this.mDisplayName);
        dest.writeString(this.mPath);
        dest.writeString(this.mMimeType);
        dest.writeInt(this.mOrientation);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeLong(this.mBucketId);
        dest.writeString(this.mBucketName);
    }

    protected LocalImage(Parcel in) {
        this.mId = in.readInt();
        this.mTakenDate = in.readLong();
        this.mDateModified = in.readLong();
        this.mSize = in.readLong();
        this.mDisplayName = in.readString();
        this.mPath = in.readString();
        this.mMimeType = in.readString();
        this.mOrientation = in.readInt();
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
        this.mBucketId = in.readLong();
        this.mBucketName = in.readString();
    }

    public static final Parcelable.Creator<LocalImage> CREATOR = new Parcelable.Creator<LocalImage>() {
        public LocalImage createFromParcel(Parcel source) {
            return new LocalImage(source);
        }

        public LocalImage[] newArray(int size) {
            return new LocalImage[size];
        }
    };
}
