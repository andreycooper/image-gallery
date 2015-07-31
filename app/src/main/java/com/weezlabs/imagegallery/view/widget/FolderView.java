package com.weezlabs.imagegallery.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weezlabs.imagegallery.R;

import static com.weezlabs.imagegallery.model.local.FolderViewModel.MAX_COUNT_IMAGES;


public class FolderView extends RelativeLayout {
    private static final String LOG_TAG = FolderView.class.getSimpleName();

    private ImageView[] mImageViews = new ImageView[MAX_COUNT_IMAGES];
    private TextView mCountTextView;

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }

    private void inflateViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_folder, this);
        mImageViews[0] = (ImageView) findViewById(R.id.top_left_image_view);
        mImageViews[1] = (ImageView) findViewById(R.id.top_right_image_view);
        mImageViews[2] = (ImageView) findViewById(R.id.bottom_left_image_view);
        mImageViews[3] = (ImageView) findViewById(R.id.bottom_right_image_view);
        mCountTextView = (TextView) findViewById(R.id.folder_view_text);
    }

    public ImageView[] getImageViews() {
        return mImageViews;
    }

    public void setCountText(String text) {
        mCountTextView.setText(text);
    }
}
