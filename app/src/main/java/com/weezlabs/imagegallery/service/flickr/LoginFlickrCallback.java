package com.weezlabs.imagegallery.service.flickr;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class LoginFlickrCallback extends FlickrCallback<Response> {
    private static final String JSON_USER_KEY = "user";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_USERNAME_KEY = "username";
    private static final String JSON_CONTENT_KEY = "_content";

    public LoginFlickrCallback(Context context, FlickrStorage flickrStorage) {
        super(context, flickrStorage);
    }

    @Override
    public void success(Response response, Response ignored) {
        String jsonBody =
                new String(((TypedByteArray) response.getBody()).getBytes());
        JsonObject json = new GsonBuilder().create()
                .fromJson(jsonBody, JsonObject.class);

        String userId = json.get(JSON_USER_KEY).getAsJsonObject()
                .get(JSON_ID_KEY).getAsString();
        String username = json.get(JSON_USER_KEY).getAsJsonObject()
                .get(JSON_USERNAME_KEY).getAsJsonObject()
                .get(JSON_CONTENT_KEY).getAsString();
        User user = new User(userId, username);
        mFlickrStorage.saveUser(user);
    }
}
