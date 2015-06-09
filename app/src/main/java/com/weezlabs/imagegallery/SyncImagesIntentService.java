package com.weezlabs.imagegallery;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.weezlabs.imagegallery.model.Folder;
import com.weezlabs.imagegallery.model.ImageFile;

import java.io.File;

import static com.weezlabs.imagegallery.db.ImageContentProvider.FOLDERS_CONTENT_URI;
import static com.weezlabs.imagegallery.db.ImageContentProvider.IMAGES_CONTENT_URI;
import static com.weezlabs.imagegallery.db.ImageContentProvider.INCORRECT_ID;


public class SyncImagesIntentService extends IntentService {
    private static final String LOG_TAG = SyncImagesIntentService.class.getSimpleName();
    private static final String ACTION_SYNC_IMAGES = "com.weezlabs.imagegallery.action.SYNC_IMAGES";

    public static final String ROOT_FOLDER = Environment.getExternalStorageDirectory().getPath();
    public static final String FOLDER = "com.weezlabs.imagegallery.extra.FOLDER";

    public static void startActionSyncImages(Context context, String folderPath) {
        Intent intent = new Intent(context.getApplicationContext(), SyncImagesIntentService.class);
        intent.setAction(ACTION_SYNC_IMAGES);
        intent.putExtra(FOLDER, folderPath);
        context.startService(intent);
    }

    private ImageFileFilter mImageFileFilter;

    public SyncImagesIntentService() {
        super("SyncImagesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC_IMAGES.equals(action)) {
                final String folderPath = intent.getStringExtra(FOLDER);
                if (TextUtils.isEmpty(folderPath)) {
                    handleActionSyncImages(ROOT_FOLDER);
                } else {
                    handleActionSyncImages(folderPath);
                }
            }
        }
    }

    private void handleActionSyncImages(String folderPath) {
        mImageFileFilter = new ImageFileFilter();
        scanFolder(folderPath);
    }

    private void scanFolder(String parentFolder) {
        File folder = new File(parentFolder);
        long folderId = INCORRECT_ID;

        File[] files = folder.listFiles(mImageFileFilter);

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file.getAbsolutePath());
            } else {
                if (folderId == INCORRECT_ID && getFolderId(folder) == INCORRECT_ID) {
                    folderId = storeParentFolderToDb(folder);
                }
                if (getImageId(file) == INCORRECT_ID) {
                    // TODO: refactor below
                    // if folderId == INCORRECT_ID read it from DB and save it
                    storeImageToDb(file, getFolderId(folder));
                }
            }
        }
    }

    private long storeParentFolderToDb(File parentFolder) {
        Folder folder = new Folder(parentFolder, true);
        ContentValues values = new Folder.Builder()
                .path(folder.getPath())
                .date(folder.getDate())
                .local(folder.isLocal()).build();
        Uri resultUri = getContentResolver()
                .insert(FOLDERS_CONTENT_URI, values);

        return ContentUris.parseId(resultUri);
    }

    private long storeImageToDb(File file, long folderId) {
        ImageFile imageFile = new ImageFile(file, true, folderId);
        ContentValues values = new ImageFile.Builder()
                .path(imageFile.getPath())
                .date(imageFile.getDate())
                .size(imageFile.getSize())
                .local(imageFile.isLocalFile())
                .folderId(imageFile.getFolderId())
                .build();
        Uri resultUri = getContentResolver()
                .insert(IMAGES_CONTENT_URI, values);

        return ContentUris.parseId(resultUri);
    }

    private long getFolderId(File folder) {
        long folderId = INCORRECT_ID;
        Cursor cursor = getContentResolver()
                .query(FOLDERS_CONTENT_URI,
                        Folder.PROJECTION_ALL,
                        Folder.PATH + "=?",
                        new String[]{folder.getAbsolutePath()},
                        null);
        if (cursor != null && cursor.moveToFirst()) {
            folderId = cursor.getLong(cursor.getColumnIndex(Folder.ID));
        }
        if (cursor != null) {
            cursor.close();
        }
        return folderId;
    }

    private long getImageId(File file) {
        long imageId = INCORRECT_ID;
        // TODO: maybe add DATE and SIZE to selection
        Cursor cursor = getContentResolver()
                .query(IMAGES_CONTENT_URI,
                        ImageFile.PROJECTION_ALL,
                        ImageFile.PATH + "=?",
                        new String[]{file.getAbsolutePath()}, null);
        if (cursor != null && cursor.moveToFirst()) {
            imageId = cursor.getLong(cursor.getColumnIndex(ImageFile.ID));
        }
        if (cursor != null) {
            cursor.close();
        }

        return imageId;
    }

}
