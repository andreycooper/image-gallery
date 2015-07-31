package com.weezlabs.imagegallery.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.twotoasters.sectioncursoradapter.adapter.SectionCursorAdapter;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.util.ImageFactory;
import com.weezlabs.imagegallery.util.TextUtils;
import com.weezlabs.imagegallery.view.adapter.viewholder.ImageViewHolder;
import com.weezlabs.imagegallery.view.adapter.viewholder.SectionViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SortedMap;


public class SectionListImageAdapter
        extends SectionCursorAdapter<String, SectionViewHolder, ImageViewHolder> {

    public SectionListImageAdapter(Context context, Cursor c) {
        super(context, c, 0, R.layout.item_section, R.layout.item_image_list);
    }

    @Override
    protected String getSectionFromCursor(Cursor cursor) throws IllegalStateException {
        long time;
        if (cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN) == -1) {
            time = cursor.getLong(cursor.getColumnIndex(Photo.TAKEN_DATE));
        } else {
            time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
        }
        String dateFormat = mContext.getString(R.string.format_date_wo_year);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(time);
        if (currentYear != calendar.get(Calendar.YEAR)) {
            dateFormat += mContext.getString(R.string.format_date_year);
        }
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat,
                mContext.getResources().getConfiguration().locale);

        return TextUtils.capitalizeFirstChar(formatter.format(calendar.getTime()));
    }

    @Override
    protected SectionViewHolder createSectionViewHolder(View view, String s) {
        return new SectionViewHolder(view);
    }

    @Override
    protected void bindSectionViewHolder(int i, SectionViewHolder sectionViewHolder, ViewGroup viewGroup, String s) {
        sectionViewHolder.sectionText.setText(s);
    }

    @Override
    protected ImageViewHolder createItemViewHolder(Cursor cursor, View view) {
        return new ImageViewHolder(view);
    }

    protected void bindItemViewHolder(ImageViewHolder holder, Cursor cursor, ViewGroup viewGroup) {
        if (holder == null) return;
        Image image = ImageFactory.buildImage(cursor);

        String imageDate = mContext.getString(R.string.label_image_date,
                TextUtils.getReadableDate(mContext, image.getTakenDate()));
        String imageSize = mContext.getString(R.string.label_image_size,
                TextUtils.getReadableFileSize(mContext, image.getSize()));

        holder.imageName.setText(image.getTitle());
        holder.imageDate.setText(imageDate);
        holder.imageSize.setText(imageSize);

        setInfoVisibility(holder);

        loadImage(mContext, holder.image, image);
    }

    public Image getImage(int clickedPosition) {
        return ImageFactory.buildImage((Cursor) getItem(clickedPosition));
    }

    protected void loadImage(Context context, ImageView imageView, Image image) {
        Glide.with(context)
                .load(image.getPath())
                .placeholder(R.drawable.ic_image_placeholder_48dp)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    private void setInfoVisibility(ImageViewHolder holder) {
        holder.imageName.setVisibility(View.VISIBLE);
        holder.infoLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected SortedMap<Integer, String> buildSections(Cursor cursor) throws IllegalStateException {
        return super.buildSections(cursor);
    }

    public void clearSections() {
        mSectionMap.clear();
    }
}
