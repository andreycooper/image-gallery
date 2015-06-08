package com.weezlabs.imagegallery.model;


import android.content.ContentValues;
import android.database.Cursor;

import java.io.File;
import java.util.HashMap;

import static com.weezlabs.imagegallery.db.DbHelper.ImageFolder.FOLDER_ID;

public class Image {
    public static final String TABLE = "images";
    public static final String ID = "_id";
    public static final String PATH = "path";
    public static final String DATE = "date";
    public static final String SIZE = "size";
    public static final String LOCAL = "local_file";

    public static final String[] PROJECTION_ALL = {
            getTableColumn(ID),
            getTableColumn(PATH),
            getTableColumn(DATE),
            getTableColumn(SIZE),
            getTableColumn(LOCAL)};

    public static final HashMap<String, String> PROJECTION_MAP = buildProjectionMap();

    private static HashMap<String, String> buildProjectionMap() {
        HashMap<String, String> projectionMap = new HashMap<>();
        projectionMap.put(ID, getTableColumn(ID));
        projectionMap.put(PATH, getTableColumn(PATH));
        projectionMap.put(DATE, getTableColumn(DATE));
        projectionMap.put(SIZE, getTableColumn(SIZE));
        projectionMap.put(LOCAL, getTableColumn(LOCAL));
        return projectionMap;
    }

    public static String getTableColumn(String column) {
        return TABLE + "." + column;
    }

    private long mId;
    private String mPath;
    private long mDate;
    private long mSize;
    private boolean mIsLocalFile;

    public Image() {
    }

    public Image(String path, long date, long size, boolean isLocal) {
        mPath = path;
        mDate = date;
        mSize = size;
        mIsLocalFile = isLocal;
    }

    public Image(File file) {
        mPath = file.getAbsolutePath();
        mDate = file.lastModified();
        mSize = file.length();
    }

    public Image(File file, boolean isLocalFile) {
        this(file);
        mIsLocalFile = isLocalFile;
    }

    public Image(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(ID));
        mPath = cursor.getString(cursor.getColumnIndex(PATH));
        mDate = cursor.getLong(cursor.getColumnIndex(DATE));
        mSize = cursor.getLong(cursor.getColumnIndex(SIZE));
        mIsLocalFile = (cursor.getInt(cursor.getColumnIndex(LOCAL)) == 1);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
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

        return getDate() == image.getDate()
                && getSize() == image.getSize()
                && getPath().equals(image.getPath());

    }

    @Override
    public int hashCode() {
        int result = getPath().hashCode();
        result = 31 * result + (int) (getDate() ^ (getDate() >>> 32));
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

        public Builder id(long id) {
            mValues.put(ID, id);
            return this;
        }

        public Builder path(String path) {
            mValues.put(PATH, path);
            return this;
        }

        public Builder date(long date) {
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

        public Builder folderId(long folderId) {
            mValues.put(FOLDER_ID, folderId);
            return this;
        }

        public ContentValues build() {
            return mValues;
        }
    }
}
