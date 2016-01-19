package com.weezlabs.imagegallery.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.util.ImageFactory;
import com.weezlabs.imagegallery.util.TextUtils;
import com.weezlabs.imagegallery.view.adapter.viewholder.ImageViewHolder;


public class ImageAdapter extends CursorAdapter {
    private int mLayoutResource;
    private boolean mIsVisibleInfo = true;

    public boolean isVisibleInfo() {
        return mIsVisibleInfo;
    }

    public void setIsVisibleInfo(boolean isVisibleInfo) {
        mIsVisibleInfo = isVisibleInfo;
    }

    public ImageAdapter(Context context, Cursor c, int layout) {
        super(context, c, true);
        mLayoutResource = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(mLayoutResource, parent, false);
        ImageViewHolder holder = new ImageViewHolder(rowView);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageViewHolder holder = (ImageViewHolder) view.getTag();

        Image image = ImageFactory.buildImage(cursor);

        String imageDate = context.getString(R.string.label_image_date,
                TextUtils.getReadableDate(context, image.getTakenDate()));
        String imageSize = context.getString(R.string.label_image_size,
                TextUtils.getReadableFileSize(context, image.getSize()));


        holder.imageName.setText(image.getTitle());
        holder.imageDate.setText(imageDate);
        holder.imageSize.setText(imageSize);

        setInfoVisibility(holder, isVisibleInfo());

        loadImage(context.getApplicationContext(), holder.image, image);
    }

    public Image getImage(int clickedPosition) {
        return ImageFactory.buildImage((Cursor) getItem(clickedPosition));
    }

    protected void loadImage(Context context, ImageView imageView, Image image) {
        Glide.with(context)
                .load(image.getPath())
                .placeholder(R.drawable.ic_image_placeholder_48dp)
                .error(R.mipmap.ic_error)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    private void setInfoVisibility(ImageViewHolder holder, boolean isVisible) {
        if (isVisible) {
            holder.imageName.setVisibility(View.VISIBLE);
            holder.infoLayout.setVisibility(View.VISIBLE);
        } else {
            holder.imageName.setVisibility(View.GONE);
            holder.infoLayout.setVisibility(View.GONE);
        }
    }

}
