package com.weezlabs.imagegallery.widget;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.weezlabs.imagegallery.model.Image;


public class StaggeredImageAdapter extends ImageAdapter {

    public StaggeredImageAdapter(Context context, Cursor c, int layout) {
        super(context, c, layout);
    }

    @Override
    protected void loadImage(Context context, ImageView imageView, Image image) {
        DynamicHeightImageView dynamicHeightImageView = (DynamicHeightImageView) imageView;
        dynamicHeightImageView.setHeightRatio((double) image.getHeight() / (double) image.getWidth());
        Glide.with(context)
                .load(image.getPath())
                .centerCrop()
                .fitCenter()
                .into(dynamicHeightImageView);
    }
}
