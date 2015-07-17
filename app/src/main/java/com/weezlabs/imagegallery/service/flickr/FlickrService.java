package com.weezlabs.imagegallery.service.flickr;

import android.content.Context;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.service.oauth.RetrofitHttpOAuthConsumer;
import com.weezlabs.imagegallery.service.oauth.SigningOkClient;
import com.weezlabs.imagegallery.util.FlickrUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class FlickrService {
    public static final String ENDPOINT_URL = "https://api.flickr.com/services/rest/";
    private FlickrApi mApi;

    public FlickrService(Context context) {
        String token = FlickrUtils.getToken(context);
        String tokenSecret = FlickrUtils.getTokenSecret(context);

        RetrofitHttpOAuthConsumer oAuthConsumer =
                new RetrofitHttpOAuthConsumer(context.getString(R.string.flickr_consumer_api_key),
                        context.getString(R.string.flickr_consumer_api_secret));
        oAuthConsumer.setTokenWithSecret(token, tokenSecret);

        OkClient client = new SigningOkClient(oAuthConsumer);
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(client)
                .build();

        mApi = adapter.create(FlickrApi.class);
    }

    public void getUserPhotos(String userId, Callback<Response> callback) {
        Map<String, String> options = new HashMap<>();
        options.put("method", "flickr.photos.search");
        options.put("user_id", userId);

        // maybe change to variable or constant
        options.put("per_page", "500");

        options.put("format", "json");
        options.put("nojsoncallback", "1");
        mApi.getUserPhotos(options, callback);
    }

    public void loginUser(Callback<Response> callback) {
        Map<String, String> options = new HashMap<>();
        options.put("method", "flickr.test.login");
        options.put("format", "json");
        options.put("nojsoncallback", "1");
        mApi.loginUser(options, callback);
    }
}
