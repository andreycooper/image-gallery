package com.weezlabs.imagegallery.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.model.local.LocalImage;
import com.weezlabs.imagegallery.util.FileUtils;


public class StaggeredImageAdapter extends ImageAdapter {

    public StaggeredImageAdapter(Context context, Cursor c) {
        super(context, c, R.layout.item_image_staggered);
        setIsVisibleInfo(false);
    }

    @Override
    protected void loadImage(Context context, ImageView imageView, Image image) {
        DynamicHeightImageView dynamicHeightImageView = (DynamicHeightImageView) imageView;
        if (image.getHeight() == 0 || image.getWidth() == 0) {
            // if MediaStore contains incorrect dimension values then try to update
            if (image instanceof LocalImage) {
                Uri uri = Uri.parse(FileUtils.FILE_SCHEME + image.getPath());
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }
            dynamicHeightImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            dynamicHeightImageView
                    .setHeightRatio((double) image.getHeight() / (double) image.getWidth());
        }
        Glide.with(context)
                .load(image.getPath())
                .placeholder(R.drawable.ic_image_placeholder_48dp)
                .centerCrop()
                .fitCenter()
                .crossFade()
                .into(dynamicHeightImageView);
    }
}
