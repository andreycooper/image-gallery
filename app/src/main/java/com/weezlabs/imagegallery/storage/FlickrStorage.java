package com.weezlabs.imagegallery.storage;


import android.content.SharedPreferences;

import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.util.TextUtils;

import javax.inject.Inject;

import oauth.signpost.OAuth;

public final class FlickrStorage {
    private static final String ICON_URL_FORMAT = "https://farm%d.staticflickr.com/%d/buddyicons/%s.jpg";
    private static final String BASE_PHOTO_URL_FORMAT = "https://farm%d.staticflickr.com/%d/";
    public static final String PHOTO_URL_FORMAT = BASE_PHOTO_URL_FORMAT + "%s_%s.jpg";
    public static final String ORIGINAL_PHOTO_URL_FORMAT = BASE_PHOTO_URL_FORMAT + "%s_%s_o.%s";

    private static final String USER_ID = "flickr_user_id";
    private static final String USERNAME = "flickr_user_name";
    private static final String USER_NSID = "flickr_user_nsid";
    private static final String USER_REAL_NAME = "flickr_user_real_name";
    private static final String ICON_FARM = "flickr_icon_farm";
    private static final String ICON_SERVER = "flickr_icon_server";
    public static final int DEF_VALUE = 0;


    private SharedPreferences mPrefs;

    @Inject
    public FlickrStorage(SharedPreferences prefs) {
        mPrefs = prefs;
    }

    public static String getPhotoUrl(Photo photo) {
        return String.format(PHOTO_URL_FORMAT, photo.getFarmId(), photo.getServerId(),
                String.valueOf(photo.getFlickrId()), photo.getSecret());
    }

    public static String getOriginalPhotoUrl(Photo photo) {
        return String.format(ORIGINAL_PHOTO_URL_FORMAT, photo.getFarmId(), photo.getServerId(),
                String.valueOf(photo.getFlickrId()), photo.getOriginalSecret(), photo.getOriginalFormat());
    }

    public static String getIconUrl(User user) {
        return String.format(ICON_URL_FORMAT,
                user.getIconFarmId(), user.getIconServerId(), String.valueOf(user.getNsId()));
    }

    public boolean isAuthenticated() {
        String token = mPrefs.getString(OAuth.OAUTH_TOKEN, null);
        String secret = mPrefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
        return TextUtils.isNonEmpty(token) && TextUtils.isNonEmpty(secret);
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN, token);
        editor.apply();
    }

    public void setTokenSecret(String tokenSecret) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN_SECRET, tokenSecret);
        editor.apply();
    }

    public String getToken() {
        return mPrefs.getString(OAuth.OAUTH_TOKEN, null);
    }

    public String getTokenSecret() {
        return mPrefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
    }

    public void resetOAuth() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(OAuth.OAUTH_TOKEN);
        editor.remove(OAuth.OAUTH_TOKEN_SECRET);
        editor.apply();
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(USER_ID, user.getFlickrId());
        editor.putString(USERNAME, user.getUsername());
        editor.putString(USER_NSID, user.getNsId());
        editor.putString(USER_REAL_NAME, user.getRealName());
        editor.putInt(ICON_FARM, user.getIconFarmId());
        editor.putInt(ICON_SERVER, user.getIconServerId());
        editor.apply();
    }

    public User restoreFlickrUser() {
        String userId = mPrefs.getString(USER_ID, null);
        String userName = mPrefs.getString(USERNAME, null);
        if (TextUtils.isNonEmpty(userId) && TextUtils.isNonEmpty(userName)) {
            User user = new User(userId, userName);
            user.setNsId(mPrefs.getString(USER_NSID, null));
            user.setRealName(mPrefs.getString(USER_REAL_NAME, null));
            user.setIconFarmId(mPrefs.getInt(ICON_FARM, DEF_VALUE));
            user.setIconServerId(mPrefs.getInt(ICON_SERVER, DEF_VALUE));
            return user;
        } else {
            return null;
        }
    }
}
