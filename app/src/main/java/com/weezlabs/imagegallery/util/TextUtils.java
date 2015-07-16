package com.weezlabs.imagegallery.util;


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
}
