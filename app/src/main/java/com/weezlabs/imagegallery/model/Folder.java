package com.weezlabs.imagegallery.model;


import android.content.ContentValues;
import android.database.Cursor;

public class Folder {
    public static final String TABLE = "folders";
    public static final String ID = "_id";
    public static final String PATH = "path";
    public static final String DATE = "date";
    public static final String LOCAL = "local_folder";

    public final static String[] PROJECTION_ALL = {ID, PATH, DATE, LOCAL};

    public static String getTableColumn(String column) {
        return TABLE + "." + column;
    }

    private int mId;
    private String mPath;
    private String mDate;
    private boolean mIsLocal;

    public Folder() {
    }

    public Folder(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(ID));
        mPath = cursor.getString(cursor.getColumnIndex(PATH));
        mDate = cursor.getString(cursor.getColumnIndex(DATE));
        mIsLocal = (cursor.getInt(cursor.getColumnIndex(LOCAL)) == 1);
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

        return getPath().equals(folder.getPath()) && getDate().equals(folder.getDate());

    }

    @Override
    public int hashCode() {
        int result = getPath().hashCode();
        result = 31 * result + getDate().hashCode();
        return result;
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

        public Builder local(boolean isLocal) {
            mValues.put(LOCAL, isLocal ? 1 : 0);
            return this;
        }

        public ContentValues build() {
            return mValues;
        }
    }
}
