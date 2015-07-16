package com.weezlabs.imagegallery.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.adapter.viewholder.ImageViewHolder;
import com.weezlabs.imagegallery.model.local.Image;


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

        Image image = new Image(cursor);

        String imageDate = context.getString(R.string.label_image_date, image.getReadableTakenDate(context));
        String imageSize = context.getString(R.string.label_image_size, image.getSize(context));

        holder.imageName.setText(image.getDisplayName());
        holder.imageDate.setText(imageDate);
        holder.imageSize.setText(imageSize);

        setInfoVisibility(holder, isVisibleInfo());

        loadImage(context, holder.image, image);
    }

    public Image getImage(int clickedPosition) {
        return new Image((Cursor) getItem(clickedPosition));
    }

    protected void loadImage(Context context, ImageView imageView, Image image) {
        Glide.with(context)
                .load(image.getPath())
                .placeholder(R.drawable.ic_image_placeholder_48dp)
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
