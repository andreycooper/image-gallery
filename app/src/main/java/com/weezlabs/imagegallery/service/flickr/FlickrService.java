package com.weezlabs.imagegallery.service.flickr;

import android.content.Context;

import com.google.gson.JsonObject;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.service.oauth.RetrofitHttpOAuthConsumer;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class FlickrService {

    private Context mAppContext;
    private FlickrStorage mFlickrStorage;
    private RetrofitHttpOAuthConsumer mOAuthConsumer;
    private FlickrApi mApi;
    private Map<String, String> mJsonParams;

    @Inject
    public FlickrService(Context context, FlickrStorage flickrStorage,
                         RetrofitHttpOAuthConsumer OAuthConsumer, FlickrApi api) {
        mAppContext = context.getApplicationContext();
        mFlickrStorage = flickrStorage;
        mOAuthConsumer = OAuthConsumer;
        mApi = api;
        mJsonParams = new HashMap<>();
        mJsonParams.put(context.getString(R.string.request_key_format),
                context.getString(R.string.request_value_json));
        mJsonParams.put(context.getString(R.string.request_key_no_json_callback),
                context.getString(R.string.request_value_no_json_callback));
    }

    public JsonObject loginUser() throws FlickrException {
        // needs for read correct token and secret from FlickrStorage
        mOAuthConsumer.setTokenWithSecret(mFlickrStorage.getToken(), mFlickrStorage.getTokenSecret());
        Map<String, String> options = getParamsWithMethod(R.string.request_value_method_login);
        return parseRawResponse(mApi.executeFlickrRequest(options));
    }

    public JsonObject getUserInfo() throws FlickrException {
        User user = mFlickrStorage.restoreFlickrUser();
        return getUserInfo(user);
    }

    public JsonObject getUserInfo(User user) throws FlickrException {
        String id = user != null ? user.getFlickrId() : "";
        return getUserInfo(id);
    }

    public JsonObject getUserInfo(String userId) throws FlickrException {
        Map<String, String> params = getParamsWithMethod(R.string.request_value_method_get_user_info);
        params.put(getString(R.string.request_keY_user_id), userId);
        return parseRawResponse(mApi.executeFlickrRequest(params));
    }

    public JsonObject getUserPhotos() throws FlickrException {
        User user = mFlickrStorage.restoreFlickrUser();
        return getUserPhotos(user);
    }

    public JsonObject getUserPhotos(User user) throws FlickrException {
        String userId = user != null ? user.getFlickrId() : "";
        return getUserPhotos(userId);
    }

    public JsonObject getUserPhotos(String userId) throws FlickrException {
        Map<String, String> params = getParamsWithMethod(R.string.request_value_method_get_user_photos);
        params.put(getString(R.string.request_keY_user_id), userId);
        return parseRawResponse(mApi.executeFlickrRequest(params));
    }

    public JsonObject getPhotoSizes(Photo photo) throws FlickrException {
        return getPhotoSizes(String.valueOf(photo.getFlickrId()));
    }

    public JsonObject getPhotoSizes(String photoId) throws FlickrException {
        Map<String, String> params = getParamsWithMethod(R.string.request_value_method_get_photo_sizes);
        params.put(getString(R.string.request_key_photo_id), photoId);
        return parseRawResponse(mApi.executeFlickrRequest(params));
    }

    public JsonObject getPhotoInfo(Photo photo) throws FlickrException {
        return getPhotoInfo(String.valueOf(photo.getFlickrId()));
    }

    public JsonObject getPhotoInfo(String photoId) throws FlickrException {
        Map<String, String> params = getParamsWithMethod(R.string.request_value_method_get_photo_info);
        params.put(getString(R.string.request_key_photo_id), photoId);
        return parseRawResponse(mApi.executeFlickrRequest(params));
    }

    private HashMap<String, String> getParamsWithMethod(int methodId) {
        return getParamsWithMethod(getString(methodId));
    }

    private HashMap<String, String> getParamsWithMethod(String method) {
        HashMap<String, String> params = new HashMap<>(mJsonParams);
        params.put(getString(R.string.request_key_method), method);
        return params;
    }

    private String getString(int stringId) {
        return mAppContext.getString(stringId);
    }

    private JsonObject parseRawResponse(JsonObject jsonObject) throws FlickrException {
        String stat = jsonObject.get(getString(R.string.json_key_stat)).getAsString();
        if (!stat.equals(getString(R.string.json_value_ok))) {
            String message = jsonObject.get(mAppContext.getString(R.string.json_key_message))
                    .getAsString();
            throw new FlickrException(message);
        }
        return jsonObject;
    }
}
