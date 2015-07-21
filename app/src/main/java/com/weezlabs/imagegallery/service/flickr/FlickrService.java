package com.weezlabs.imagegallery.service.flickr;

import com.weezlabs.imagegallery.service.oauth.RetrofitHttpOAuthConsumer;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.client.Response;


public class FlickrService {
    private FlickrStorage mFlickrStorage;
    private RetrofitHttpOAuthConsumer mOAuthConsumer;
    private FlickrApi mApi;
    private Map<String, String> mJsonOptions;

    @Inject
    public FlickrService(FlickrStorage flickrStorage, RetrofitHttpOAuthConsumer OAuthConsumer,
                         FlickrApi api) {
        mFlickrStorage = flickrStorage;
        mOAuthConsumer = OAuthConsumer;
        mApi = api;
        mJsonOptions = new HashMap<>();
        mJsonOptions.put("format", "json");
        mJsonOptions.put("nojsoncallback", "1");
    }

    public void getUserPhotos(String userId, Callback<Response> callback) {
        Map<String, String> options = new HashMap<>(mJsonOptions);
        options.put("method", "flickr.photos.search");
        options.put("user_id", userId);
        mApi.getUserPhotos(options, callback);
    }

    public void loginUser(Callback<Response> callback) {
        // needs for read correct token and secret from FlickrStorage
        mOAuthConsumer.setTokenWithSecret(mFlickrStorage.getToken(), mFlickrStorage.getTokenSecret());
        Map<String, String> options = new HashMap<>(mJsonOptions);
        options.put("method", "flickr.test.login");
        mApi.loginUser(options, callback);
    }
}
