package com.weezlabs.imagegallery.model;


import android.content.ContentValues;
import android.database.Cursor;

import java.io.File;
import java.util.HashMap;

public class Folder {
    public static final String TABLE = "folders";
    public static final String ID = "_id";
    public static final String PATH = "path";
    public static final String DATE = "date";
    public static final String LOCAL = "local_folder";

    public static final String[] PROJECTION_ALL = {
            getTableColumn(ID),
            getTableColumn(PATH),
            getTableColumn(DATE),
            getTableColumn(LOCAL)};

    public static final HashMap<String, String> PROJECTION_MAP = buildProjectionMap();

    private static HashMap<String, String> buildProjectionMap() {
        HashMap<String, String> projectionMap = new HashMap<>();
        projectionMap.put(ID, getTableColumn(ID));
        projectionMap.put(PATH, getTableColumn(PATH));
        projectionMap.put(DATE, getTableColumn(DATE));
        projectionMap.put(LOCAL, getTableColumn(LOCAL));
        return projectionMap;
    }

    public static String getTableColumn(String column) {
        return TABLE + "." + column;
    }

    private long mId;
    private String mPath;
    private long mDate;
    private boolean mIsLocal;

    public Folder() {
    }

    public Folder(String path, long date, boolean isLocal) {
        mPath = path;
        mDate = date;
        mIsLocal = isLocal;
    }

    public Folder(File file) {
        mPath = file.getAbsolutePath();
        mDate = file.lastModified();
    }

    public Folder(File file, boolean isLocal) {
        this(file);
        mIsLocal = isLocal;
    }

    public Folder(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(ID));
        mPath = cursor.getString(cursor.getColumnIndex(PATH));
        mDate = cursor.getLong(cursor.getColumnIndex(DATE));
        mIsLocal = (cursor.getInt(cursor.getColumnIndex(LOCAL)) == 1);
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

    public boolean isLocal() {
        return mIsLocal;
    }

    public void setIsLocal(boolean isLocal) {
        mIsLocal = isLocal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Folder folder = (Folder) o;

        return getDate() == folder.getDate() && getPath().equals(folder.getPath());

    }

    @Override
    public int hashCode() {
        int result = getPath().hashCode();
        result = 31 * result + (int) (getDate() ^ (getDate() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Folder{");
        sb.append("mId=").append(mId);
        sb.append(", mPath='").append(mPath).append('\'');
        sb.append(", mDate=").append(mDate);
        sb.append(", mIsLocal=").append(mIsLocal);
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

        public Builder local(boolean isLocal) {
            mValues.put(LOCAL, isLocal ? 1 : 0);
            return this;
        }

        public ContentValues build() {
            return mValues;
        }
    }
}
