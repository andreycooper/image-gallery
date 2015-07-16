package com.weezlabs.imagegallery.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.twotoasters.sectioncursoradapter.adapter.SectionCursorAdapter;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.adapter.viewholder.ImageViewHolder;
import com.weezlabs.imagegallery.adapter.viewholder.SectionViewHolder;
import com.weezlabs.imagegallery.model.local.Image;
import com.weezlabs.imagegallery.util.TextUtils;

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
        long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
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
        Image image = new Image(cursor);

        String imageDate = mContext.getString(R.string.label_image_date, image.getReadableTakenDate(mContext));
        String imageSize = mContext.getString(R.string.label_image_size, image.getSize(mContext));

        holder.imageName.setText(image.getDisplayName());
        holder.imageDate.setText(imageDate);
        holder.imageSize.setText(imageSize);

        setInfoVisibility(holder);

        loadImage(mContext, holder.image, image);
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
