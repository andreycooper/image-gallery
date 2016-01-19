package com.weezlabs.imagegallery.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.imagegallery.model.flickr.Photo;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flickr_photos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_FLICKR_PHOTOS_TABLE = "" +
            "CREATE TABLE " + Photo.TABLE + "(" +
            Photo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Photo.FLICKR_ID + " INTEGER NOT NULL," +
            Photo.OWNER + " TEXT NOT NULL," +
            Photo.SECRET + " TEXT NOT NULL," +
            Photo.SERVER + " INTEGER NOT NULL," +
            Photo.FARM + " INTEGER NOT NULL," +
            Photo.TITLE + " TEXT NOT NULL," +
            Photo.PUBLIC + " INTEGER NOT NULL," +
            Photo.FRIEND + " INTEGER NOT NULL," +
            Photo.FAMILY + " INTEGER NOT NULL," +
            Photo.ROTATION + " INTEGER," +
            Photo.ORIGINAL_SECRET + " TEXT," +
            Photo.ORIGINAL_FORMAT + " TEXT," +
            Photo.TAKEN_DATE + " INTEGER," +
            Photo.LAST_UPDATE + " INTEGER," +
            Photo.WIDTH + " INTEGER," +
            Photo.HEIGHT + " INTEGER)";

    public static final String UPGRADE__FLICKR_PHOTOS_TABLE = "" +
            "DROP TABLE IF EXISTS " + Photo.TABLE;


    public DbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FLICKR_PHOTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPGRADE__FLICKR_PHOTOS_TABLE);
        onCreate(db);
    }
}
