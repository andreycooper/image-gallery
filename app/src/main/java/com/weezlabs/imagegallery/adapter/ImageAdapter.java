package com.weezlabs.imagegallery.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;


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

        String imageDate = context.getString(R.string.label_image_date, image.getTakenDate(context));
        String imageSize = context.getString(R.string.label_image_size, image.getSize(context));

        holder.mImageName.setText(image.getDisplayName());
        holder.mImageDate.setText(imageDate);
        holder.mImageSize.setText(imageSize);

        setInfoVisibility(holder, isVisibleInfo());

        loadImage(context, holder.mImage, image);
    }

    public Image getImage(int clickedPosition) {
        return new Image((Cursor) getItem(clickedPosition));
    }

    protected void loadImage(Context context, ImageView imageView, Image image) {
        Glide.with(context)
                .load(image.getPath())
                .placeholder(R.drawable.ic_image_placeholder_48dp)
                .centerCrop()
                .into(imageView);
    }

    private void setInfoVisibility(ImageViewHolder holder, boolean isVisible) {
        if (isVisible) {
            holder.mImageName.setVisibility(View.VISIBLE);
            holder.mInfoLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mImageName.setVisibility(View.GONE);
            holder.mInfoLayout.setVisibility(View.GONE);
        }
    }

    public static class ImageViewHolder {
        ImageView mImage;
        View mInfoLayout;
        TextView mImageName;
        TextView mImageDate;
        TextView mImageSize;

        public ImageViewHolder(View view) {
            mImage = (ImageView) view.findViewById(R.id.image_view);
            mInfoLayout = view.findViewById(R.id.info_layout);
            mImageName = (TextView) view.findViewById(R.id.image_name_text_view);
            mImageDate = (TextView) view.findViewById(R.id.image_date_text_view);
            mImageSize = (TextView) view.findViewById(R.id.image_size_text_view);
        }

    }
}
