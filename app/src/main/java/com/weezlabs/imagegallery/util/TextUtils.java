package com.weezlabs.imagegallery.util;


import android.content.Context;
import android.text.format.Formatter;

import com.weezlabs.imagegallery.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class TextUtils {

    private TextUtils() {
        // prevent creation of instance
    }

    public static boolean isNonEmpty(String string) {
        return !android.text.TextUtils.isEmpty(string);
    }

    public static String capitalizeFirstChar(String string) {
        StringBuilder builder =
                new StringBuilder(string);
        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        return builder.toString();
    }

    public static String getReadableDate(Context context, long dateInMillis) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.format_image_date),
                context.getResources().getConfiguration().locale);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        return formatter.format(calendar.getTime());
    }

    public static String getReadableFileSize(Context context, long fileSize) {
        return Formatter.formatShortFileSize(context, fileSize);
    }
}
