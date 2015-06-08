package com.weezlabs.imagegallery;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.weezlabs.imagegallery.db.ImageContentProvider;
import com.weezlabs.imagegallery.model.Image;

import java.lang.ref.WeakReference;

import static com.weezlabs.imagegallery.db.ImageContentProvider.INCORRECT_ID;


class ImageCursorLoadTask extends AsyncTask<Void, Void, Cursor> {
    private WeakReference<Context> mContextWeakReference;
    private long mFolderId = INCORRECT_ID;
    private WeakReference<OnCursorLoadCompletedListener> mListenerWeakReference;

    public ImageCursorLoadTask(Context context, long folderId, OnCursorLoadCompletedListener listener) {
        mContextWeakReference = new WeakReference<>(context);
        mFolderId = folderId;
        mListenerWeakReference = new WeakReference<>(listener);
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        Context context = mContextWeakReference.get();
        if (context != null && mFolderId != INCORRECT_ID) {
            ContentResolver contentResolver = context.getContentResolver();
            return contentResolver.query(ImageContentProvider.buildFolderImagesUri(mFolderId),
                    Image.PROJECTION_ALL, null, null, null);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        OnCursorLoadCompletedListener listener = mListenerWeakReference.get();
        if (listener != null) {
            listener.onCursorLoadCompleted(cursor);
        }
    }

    public interface OnCursorLoadCompletedListener {
        void onCursorLoadCompleted(Cursor cursor);
    }
}
