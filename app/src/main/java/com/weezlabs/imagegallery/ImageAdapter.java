package com.weezlabs.imagegallery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.model.Image;


public class ImageAdapter extends CursorAdapter {
    private int mLayoutResource;

    public ImageAdapter(Context context, Cursor c, int layout) {
        super(context, c, true);
        mLayoutResource = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(mLayoutResource, parent, false);
        ViewHolder holder = new ViewHolder(rowView);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        Image image = new Image(cursor);

        String imageDate = context.getString(R.string.label_image_date, image.getTakenDate(context));
        String imageSize = context.getString(R.string.label_image_size, image.getSize(context));

        holder.mImageName.setText(image.getDisplayName());
        holder.mImageDate.setText(imageDate);
        holder.mImageSize.setText(imageSize);

        Glide.with(context)
                .load(image.getPath())
                .centerCrop()
                .thumbnail(0.3f)
                .into(holder.mImage);
    }

    public static class ViewHolder {
        ImageView mImage;
        TextView mImageName;
        TextView mImageDate;
        TextView mImageSize;

        public ViewHolder(View view) {
            mImage = (ImageView) view.findViewById(R.id.image_view);
            mImageName = (TextView) view.findViewById(R.id.image_name_text_view);
            mImageDate = (TextView) view.findViewById(R.id.image_date_text_view);
            mImageSize = (TextView) view.findViewById(R.id.image_size_text_view);
        }

    }
}
