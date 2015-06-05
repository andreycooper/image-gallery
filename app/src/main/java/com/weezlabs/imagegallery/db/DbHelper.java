package com.weezlabs.imagegallery.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.imagegallery.model.Folder;
import com.weezlabs.imagegallery.model.Image;

import java.util.HashMap;


public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "images_db";

    private static final String CREATE_IMAGE_TABLE = "" +
            "CREATE TABLE " + Image.TABLE + "(" +
            Image.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Image.PATH + " TEXT NOT NULL," +
            Image.DATE + " INTEGER NOT NULL," +
            Image.SIZE + " INTEGER NOT NULL," +
            Image.LOCAL + " INTEGER NOT NULL)";

    private static final String CREATE_FOLDER_TABLE = "" +
            "CREATE TABLE " + Folder.TABLE + "(" +
            Folder.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Folder.PATH + " TEXT NOT NULL," +
            Folder.DATE + " INTEGER NOT NULL," +
            Folder.LOCAL + " INTEGER NOT NULL)";

    private static final String CREATE_IMAGE_FOLDER_TABLE = "" +
            "CREATE TABLE " + ImageFolder.TABLE + "(" +
            ImageFolder.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ImageFolder.FOLDER_ID + " INTEGER NOT NULL," +
            ImageFolder.IMAGE_ID + " INTEGER NOT NULL)";


    private static final String UPGRADE_IMAGE_TABLE = "" +
            "DROP TABLE IF EXISTS " + Image.TABLE;

    private static final String UPGRADE_FOLDER_TABLE = "" +
            "DROP TABLE IF EXISTS " + Folder.TABLE;

    private static final String UPGRADE_IMAGE_FOLDER_TABLE = "" +
            "DROP TABLE IF EXISTS " + ImageFolder.TABLE;

    public DbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_FOLDER_TABLE);
        db.execSQL(CREATE_IMAGE_FOLDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPGRADE_IMAGE_TABLE);
        db.execSQL(UPGRADE_FOLDER_TABLE);
        db.execSQL(UPGRADE_IMAGE_FOLDER_TABLE);
        onCreate(db);
    }

    public static class ImageFolder {
        public static final String TABLE = "image_folder";
        public static final String ID = "_id";
        public static final String FOLDER_ID = "folder_id";
        public static final String IMAGE_ID = "image_id";

        public static final HashMap<String, String> PROJECTION_MAP = buildProjectionMap();

        private static HashMap<String, String> buildProjectionMap() {
            HashMap<String, String> projectionMap = new HashMap<>();
            projectionMap.put(ID, getTableColumn(ID));
            projectionMap.put(FOLDER_ID, getTableColumn(FOLDER_ID));
            projectionMap.put(IMAGE_ID, getTableColumn(IMAGE_ID));
            return projectionMap;
        }

        public static String getTableColumn(String column) {
            return TABLE + "." + column;
        }

        public static final class Builder {
            private final ContentValues mValues = new ContentValues();

            public Builder id(long id) {
                mValues.put(ID, id);
                return this;
            }

            public Builder folderId(long folderId) {
                mValues.put(FOLDER_ID, folderId);
                return this;
            }

            public Builder imageId(long imageId) {
                mValues.put(IMAGE_ID, imageId);
                return this;
            }

            public ContentValues build() {
                return mValues;
            }
        }
    }
}
