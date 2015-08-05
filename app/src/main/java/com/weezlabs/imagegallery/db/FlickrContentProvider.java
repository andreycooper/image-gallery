package com.weezlabs.imagegallery.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.weezlabs.imagegallery.model.flickr.Photo;

public class FlickrContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.weezlabs.imagegallery.flickr.provider";
    private static final String SCHEME = "content://";
    private static final String UNKNOWN_URI = "Unknown URI";

    private static final String PHOTOS_PATH = "photos";
    private static final String DELETE_ALL_PHOTOS_PATH = "delete";

    private static final int PHOTOS = 10;
    private static final int PHOTO_ID = 11;
    private static final int PHOTOS_DELETE = 13;

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);
    public static final Uri PHOTOS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PHOTOS_PATH).build();
    public static final Uri PHOTOS_DELETE_CONTENT_URI = PHOTOS_CONTENT_URI.buildUpon()
            .appendPath(DELETE_ALL_PHOTOS_PATH).build();

    public static Uri buildPhotoIdUri(long photoId) {
        return PHOTOS_CONTENT_URI.buildUpon().appendPath(String.valueOf(photoId)).build();
    }


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;

        matcher.addURI(authority, "photos", PHOTOS);
        matcher.addURI(authority, "photos/#", PHOTO_ID);
        matcher.addURI(authority, "photos/delete", PHOTOS_DELETE);

        return matcher;
    }

    private DbHelper mDbHelper;


    public FlickrContentProvider() {
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        throw null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri resultUri;
        long rowId;
        switch (match) {
            case PHOTOS:
                rowId = mDbHelper.getWritableDatabase()
                        .insert(Photo.TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }

        resultUri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        String groupByString = "";
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {
            case PHOTOS:
                queryBuilder.setTables(Photo.TABLE);
                break;
            case PHOTO_ID:
                queryBuilder.setTables(Photo.TABLE);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }
        cursor = queryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, groupByString, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case PHOTOS:
                count = mDbHelper.getWritableDatabase()
                        .update(Photo.TABLE, values, selection, selectionArgs);
                break;
            case PHOTO_ID:
                selection = getPhotoSelection(uri, selection);
                count = mDbHelper.getWritableDatabase()
                        .update(Photo.TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case PHOTOS:
                count = mDbHelper.getWritableDatabase()
                        .delete(Photo.TABLE, selection, selectionArgs);
                break;
            case PHOTO_ID:
                selection = getPhotoSelection(uri, selection);
                count = mDbHelper.getWritableDatabase()
                        .delete(Photo.TABLE, selection, selectionArgs);
                break;
            case PHOTOS_DELETE:
                count = mDbHelper.getWritableDatabase()
                        .delete(Photo.TABLE, null, null);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private String getPhotoSelection(Uri uri, String selection) {
        String photoId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
            selection = Photo.ID + "=" + photoId;
        } else {
            selection = selection + " AND " + Photo.ID + "=" + photoId;
        }
        return selection;
    }
}
