package com.weezlabs.imagegallery.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.weezlabs.imagegallery.model.Folder;
import com.weezlabs.imagegallery.model.ImageFile;

import java.util.ArrayList;
import java.util.List;

public class ImageContentProvider extends ContentProvider {
    private static final String LOG_TAG = ImageContentProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.weezlabs.imagegallery.provider";
    private static final String SCHEME = "content://";
    private static final String UNKNOWN_URI = "Unknown URI";

    private static final String IMAGES_PATH = "images";
    private static final String FOLDERS_PATH = "folders";

    private static final int IMAGES = 10;
    private static final int IMAGE_ID = 11;

    private static final int FOLDERS = 20;
    private static final int FOLDER_ID = 21;
    private static final int FOLDER_IMAGES = 22;

    private static final int FOLDER_ID_POSITION = 1;

    public static final long INCORRECT_ID = -1;

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final Uri IMAGES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(IMAGES_PATH).build();

    public static final Uri FOLDERS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(FOLDERS_PATH).build();

    public static Uri buildImageIdUri(long imageId) {
        return IMAGES_CONTENT_URI.buildUpon().appendPath(String.valueOf(imageId)).build();
    }

    public static Uri buildFolderIdUri(long folderId) {
        return FOLDERS_CONTENT_URI.buildUpon().appendPath(String.valueOf(folderId)).build();
    }

    public static Uri buildFolderImagesUri(long folderId) {
        return buildFolderIdUri(folderId).buildUpon().appendPath(IMAGES_PATH).build();
    }

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;

        matcher.addURI(authority, "images", IMAGES);
        matcher.addURI(authority, "images/#", IMAGE_ID);
        matcher.addURI(authority, "folders", FOLDERS);
        matcher.addURI(authority, "folders/#", FOLDER_ID);
        matcher.addURI(authority, "folders/#/images", FOLDER_IMAGES);

        return matcher;
    }

    private DbHelper mDbHelper;

    public ImageContentProvider() {
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
            case FOLDERS:
                rowId = mDbHelper.getWritableDatabase().insert(Folder.TABLE, null, values);
                break;
            case IMAGES:
                rowId = mDbHelper.getWritableDatabase().insert(ImageFile.TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }
        resultUri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case IMAGES:
                count = mDbHelper.getWritableDatabase()
                        .update(ImageFile.TABLE, values, selection, selectionArgs);
                break;
            case IMAGE_ID:
                selection = getImageSelection(uri, selection);
                count = mDbHelper.getWritableDatabase()
                        .update(ImageFile.TABLE, values, selection, selectionArgs);
                break;
            case FOLDERS:
                count = mDbHelper.getWritableDatabase()
                        .update(Folder.TABLE, values, selection, selectionArgs);
                break;
            case FOLDER_ID:
                selection = getFolderSelection(uri, selection);
                count = mDbHelper.getWritableDatabase()
                        .update(Folder.TABLE, values, selection, selectionArgs);
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
        int countRows;
        switch (match) {
            case IMAGE_ID:
                selection = getImageSelection(uri, selection);
                countRows = mDbHelper.getWritableDatabase().delete(ImageFile.TABLE, selection, selectionArgs);
                break;
            case FOLDER_ID:
                selection = getFolderSelection(uri, selection);
                countRows = mDbHelper.getWritableDatabase().delete(Folder.TABLE, selection, selectionArgs);
                // clean link in "images" table
                if (countRows > 0) {
                    String folderId = uri.getLastPathSegment();
                    // query all images in this folder from "images" table
                    // and use applyBatch to delete all images
                    List<Long> imageIdList = getImageIdList(folderId);
                    ArrayList<ContentProviderOperation> operationList =
                            getDeleteImageOperations(imageIdList);
                    try {
                        applyBatch(operationList);
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "Can't delete image!");
                    }
                }

                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }
        return countRows;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        String groupByString = "";
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {
            case IMAGES:
                queryBuilder.setTables(ImageFile.TABLE);
                break;
            case FOLDERS:
                queryBuilder.setTables(Folder.TABLE);
                break;
            case FOLDER_IMAGES:
                queryBuilder.setTables(ImageFile.TABLE);
                queryBuilder.appendWhere(ImageFile.FOLDER_ID + "="
                        + uri.getPathSegments().get(FOLDER_ID_POSITION));
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI + uri.toString());
        }
        cursor = queryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, groupByString, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private String getImageSelection(Uri uri, String selection) {
        String bookId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
            selection = ImageFile.ID + "=" + bookId;
        } else {
            selection = selection + " AND " + ImageFile.ID + "=" + bookId;
        }
        return selection;
    }

    private String getFolderSelection(Uri uri, String selection) {
        String userId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
            selection = Folder.ID + "=" + userId;
        } else {
            selection = selection + " AND " + Folder.ID + "=" + userId;
        }
        return selection;
    }

    private List<Long> getImageIdList(String folderId) {
        List<Long> imageIdList = new ArrayList<>();
        long imageId;
        Cursor cursor = mDbHelper.getReadableDatabase()
                .query(ImageFile.TABLE,
                        new String[]{ImageFile.ID},
                        ImageFile.FOLDER_ID + "=?",
                        new String[]{folderId},
                        null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                imageId = cursor.getLong(cursor.getColumnIndex(ImageFile.ID));
                imageIdList.add(imageId);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return imageIdList;
    }

    private ArrayList<ContentProviderOperation> getDeleteImageOperations(List<Long> imageIdList) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<>();
        for (Long imageId : imageIdList) {
            operationList.add(ContentProviderOperation.newDelete(buildImageIdUri(imageId))
                    .withYieldAllowed(true).build());
        }
        return operationList;
    }
}
