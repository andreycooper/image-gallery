package com.weezlabs.imagegallery.model;


import android.content.ContentValues;
import android.database.Cursor;

public class Image {
    public static final String TABLE = "images";
    public static final String ID = "_id";
    public static final String PATH = "path";
    public static final String DATE = "date";
    public static final String SIZE = "size";
    public static final String LOCAL = "local_file";

    public final static String[] PROJECTION_ALL = {ID, PATH, DATE, SIZE, LOCAL};

    public static String getTableColumn(String column) {
        return TABLE + "." + column;
    }

    private int mId;
    private String mPath;
    private String mDate;
    private long mSize;
    private boolean mIsLocalFile;

    public Image() {
    }

    public Image(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(ID));
        mPath = cursor.getString(cursor.getColumnIndex(PATH));
        mDate = cursor.getString(cursor.getColumnIndex(DATE));
        mSize = cursor.getLong(cursor.getColumnIndex(SIZE));
        mIsLocalFile = (cursor.getInt(cursor.getColumnIndex(LOCAL)) == 1);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public boolean isLocalFile() {
        return mIsLocalFile;
    }

    public void setIsLocalFile(boolean isLocalFile) {
        mIsLocalFile = isLocalFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        return getSize() == image.getSize()
                && getPath().equals(image.getPath())
                && getDate().equals(image.getDate());

    }

    @Override
    public int hashCode() {
        int result = getPath().hashCode();
        result = 31 * result + getDate().hashCode();
        result = 31 * result + (int) (getSize() ^ (getSize() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Image{");
        sb.append("mId=").append(mId);
        sb.append(", mPath='").append(mPath).append('\'');
        sb.append(", mDate='").append(mDate).append('\'');
        sb.append(", mSize=").append(mSize);
        sb.append(", mIsLocalFile=").append(mIsLocalFile);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private final ContentValues mValues = new ContentValues();

        public Builder id(int id) {
            mValues.put(ID, id);
            return this;
        }

        public Builder path(String path) {
            mValues.put(PATH, path);
            return this;
        }

        public Builder date(String date) {
            mValues.put(DATE, date);
            return this;
        }

        public Builder size(long size) {
            mValues.put(SIZE, size);
            return this;
        }

        public Builder local(boolean isLocal) {
            mValues.put(LOCAL, isLocal ? 1 : 0);
            return this;
        }

        public ContentValues build() {
            return mValues;
        }
    }
}
