package com.weezlabs.imagegallery.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.User;

import oauth.signpost.OAuth;

public final class FlickrUtils {
    private static final String BASE_PHOTO_URL_FORMAT = "https://farm%d.staticflickr.com/%d/";
    public static final String PHOTO_URL_FORMAT = BASE_PHOTO_URL_FORMAT + "%s_%s_b.jpg";
    public static final String ORIGINAL_PHOTO_URL_FORMAT = BASE_PHOTO_URL_FORMAT + "%s_%s_o.%s";
    private static final String FLICKR_USER_ID = "flickr_user_id";
    private static final String FLICKR_USERNAME = "flickr_user_name";

    private FlickrUtils() {
    }

    public static String getPhotoUrl(Photo photo) {
        return String.format(PHOTO_URL_FORMAT, photo.getFarmId(), photo.getServerId(),
                photo.getExternalId(), photo.getSecret());
    }

    public static boolean isAuthenticated(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        String token = prefs.getString(OAuth.OAUTH_TOKEN, null);
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
        return TextUtils.isNonEmpty(token) && TextUtils.isNonEmpty(secret);
    }

    public static void setToken(Context context, String token) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN, token);
        editor.apply();
    }

    public static void setTokenSecret(Context context, String tokenSecret) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN_SECRET, tokenSecret);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        return prefs.getString(OAuth.OAUTH_TOKEN, null);
    }

    public static String getTokenSecret(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        return prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
    }

    public static void resetOAuth(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(OAuth.OAUTH_TOKEN);
        editor.remove(OAuth.OAUTH_TOKEN_SECRET);
        editor.apply();
    }

    public static void saveUser(Context context, User user) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FLICKR_USER_ID, user.getFlickrId());
        editor.putString(FLICKR_USERNAME, user.getUsername());
        editor.apply();
    }

    public static User restoreFlickrUser(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        String userId = prefs.getString(FLICKR_USER_ID, null);
        String userName = prefs.getString(FLICKR_USERNAME, null);
        if (TextUtils.isNonEmpty(userId) && TextUtils.isNonEmpty(userName)) {
            return new User(userId, userName);
        } else {
            return null;
        }
    }
}
