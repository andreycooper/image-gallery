package com.weezlabs.imagegallery.storage;


import android.content.SharedPreferences;

import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.util.TextUtils;

import javax.inject.Inject;

import oauth.signpost.OAuth;

public final class FlickrStorage {
    private static final String BASE_PHOTO_URL_FORMAT = "https://farm%d.staticflickr.com/%d/";
    public static final String PHOTO_URL_FORMAT = BASE_PHOTO_URL_FORMAT + "%s_%s_b.jpg";
    public static final String ORIGINAL_PHOTO_URL_FORMAT = BASE_PHOTO_URL_FORMAT + "%s_%s_o.%s";
    private static final String FLICKR_USER_ID = "flickr_user_id";
    private static final String FLICKR_USERNAME = "flickr_user_name";

    private SharedPreferences mPrefs;

    @Inject
    public FlickrStorage(SharedPreferences prefs){
        mPrefs = prefs;
    }

    public static String getPhotoUrl(Photo photo) {
        return String.format(PHOTO_URL_FORMAT, photo.getFarmId(), photo.getServerId(),
                photo.getExternalId(), photo.getSecret());
    }

    public boolean isAuthenticated() {
        String token = mPrefs.getString(OAuth.OAUTH_TOKEN, null);
        String secret = mPrefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
        return TextUtils.isNonEmpty(token) && TextUtils.isNonEmpty(secret);
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN, token);
        editor.commit();
    }

    public void setTokenSecret(String tokenSecret) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(OAuth.OAUTH_TOKEN_SECRET, tokenSecret);
        editor.commit();
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
        editor.commit();
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(FLICKR_USER_ID, user.getFlickrId());
        editor.putString(FLICKR_USERNAME, user.getUsername());
        editor.commit();
    }

    public User restoreFlickrUser() {
        String userId = mPrefs.getString(FLICKR_USER_ID, null);
        String userName = mPrefs.getString(FLICKR_USERNAME, null);
        if (TextUtils.isNonEmpty(userId) && TextUtils.isNonEmpty(userName)) {
            return new User(userId, userName);
        } else {
            return null;
        }
    }
}
