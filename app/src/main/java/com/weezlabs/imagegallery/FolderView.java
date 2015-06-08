package com.weezlabs.imagegallery;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weezlabs.imagegallery.ImageCursorLoadTask.OnCursorLoadCompletedListener;
import com.weezlabs.imagegallery.model.Image;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static com.weezlabs.imagegallery.db.ImageContentProvider.INCORRECT_ID;


public class FolderView extends RelativeLayout implements OnCursorLoadCompletedListener {
    private static final String LOG_TAG = FolderView.class.getSimpleName();

    private ImageView mTopLeftImageView;
    private ImageView mTopRightImageView;
    private ImageView mBottomLeftImageView;
    private ImageView mBottomRightImageView;

    private long mFolderId = INCORRECT_ID;

    public FolderView(Context context) {
        super(context);
        inflateViews(context);
    }

    public FolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateViews(context);
    }

    public FolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateViews(context);
    }

    private void inflateViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_folder, this);
        mTopLeftImageView = (ImageView) findViewById(R.id.top_left_image_view);
        mTopRightImageView = (ImageView) findViewById(R.id.top_right_image_view);
        mBottomLeftImageView = (ImageView) findViewById(R.id.bottom_left_image_view);
        mBottomRightImageView = (ImageView) findViewById(R.id.bottom_right_image_view);
    }

    public long getFolderId() {
        return mFolderId;
    }

    public void setFolderId(long folderId) {
        mFolderId = folderId;
        new ImageCursorLoadTask(getContext(), mFolderId, this)
                .executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onCursorLoadCompleted(Cursor cursor) {
        logCursor(cursor);
    }

    private void logCursor(Cursor cursor) {
        Image image;
        Log.i(LOG_TAG, "Folder id: " + mFolderId);
        Log.i(LOG_TAG, "=== images in this folder ===");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                image = new Image(cursor);
                Log.i(LOG_TAG, image.toString());
            } while (cursor.moveToNext());
        }
        Log.i(LOG_TAG, "=============================");
    }


}
