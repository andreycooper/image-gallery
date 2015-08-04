package com.weezlabs.imagegallery.model.local;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;


public class Bucket implements Parcelable {

    public static final long FLICKR_BUCKET_ID = -993;

    private long mBucketId;
    private String mBucketName;

    public Bucket(Cursor cursor) {
        mBucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        mBucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
    }

    public Bucket(long bucketId, String bucketName) {
        mBucketId = bucketId;
        mBucketName = bucketName;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mBucketId);
        dest.writeString(this.mBucketName);
    }

    protected Bucket(Parcel in) {
        this.mBucketId = in.readLong();
        this.mBucketName = in.readString();
    }

    public static final Parcelable.Creator<Bucket> CREATOR = new Parcelable.Creator<Bucket>() {
        public Bucket createFromParcel(Parcel source) {
            return new Bucket(source);
        }

        public Bucket[] newArray(int size) {
            return new Bucket[size];
        }
    };
}
