package com.weezlabs.imagegallery.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weezlabs.imagegallery.model.Folder;
import com.weezlabs.imagegallery.model.ImageFile;


public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "images_db";

    private static final String CREATE_IMAGE_TABLE = "" +
            "CREATE TABLE " + ImageFile.TABLE + "(" +
            ImageFile.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ImageFile.PATH + " TEXT NOT NULL," +
            ImageFile.DATE + " INTEGER NOT NULL," +
            ImageFile.SIZE + " INTEGER NOT NULL," +
            ImageFile.LOCAL + " INTEGER NOT NULL," +
            ImageFile.FOLDER_ID + " INTEGER NOT NULL)";

    private static final String CREATE_FOLDER_TABLE = "" +
            "CREATE TABLE " + Folder.TABLE + "(" +
            Folder.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Folder.PATH + " TEXT NOT NULL," +
            Folder.DATE + " INTEGER NOT NULL," +
            Folder.LOCAL + " INTEGER NOT NULL)";


    private static final String UPGRADE_IMAGE_TABLE = "" +
            "DROP TABLE IF EXISTS " + ImageFile.TABLE;

    private static final String UPGRADE_FOLDER_TABLE = "" +
            "DROP TABLE IF EXISTS " + Folder.TABLE;


    public DbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_FOLDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UPGRADE_IMAGE_TABLE);
        db.execSQL(UPGRADE_FOLDER_TABLE);
        onCreate(db);
    }

}
