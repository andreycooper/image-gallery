package com.weezlabs.imagegallery.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.util.TextUtils;
import com.weezlabs.imagegallery.view.adapter.viewholder.SectionViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;


public class SectionGridImageAdapter extends ImageAdapter
        implements StickyGridHeadersSimpleAdapter {

    private static final String LOG_TAG = SectionGridImageAdapter.class.getSimpleName();

    public SectionGridImageAdapter(Context context, Cursor c) {
        super(context, c, R.layout.item_image_grid);
        setIsVisibleInfo(false);
        Timber.tag(LOG_TAG);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        SectionViewHolder holder;
        if (convertView != null) {
            holder = (SectionViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_section, parent, false);
            holder = new SectionViewHolder(convertView);
            convertView.setTag(holder);
        }

        Cursor cursor = getCursor();
        String headerTitle = getSectionHeaderFromCursor(cursor, position);

        holder.sectionText.setText(headerTitle);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        String header = getSectionHeaderFromCursor(getCursor(), position);
        if (TextUtils.isNonEmpty(header)) {
            return getHeaderId(header);
        }
        return 0;
    }

    private String getSectionHeaderFromCursor(Cursor cursor, int position)
            throws IllegalStateException {
        if (cursor != null && !cursor.isClosed() && cursor.moveToPosition(position)) {
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
        } else {
            return null;
        }
    }

    private long getHeaderId(String header) {
        long id = 0;
        for (char ch : header.toCharArray()) {
            id += ch;
        }
        return id;
    }
}
