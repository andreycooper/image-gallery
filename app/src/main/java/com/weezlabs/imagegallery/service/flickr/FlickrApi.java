package com.weezlabs.imagegallery.service.flickr;


import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface FlickrApi {
    // TODO: declare methods for getting photos info
    @GET("/")
    void getUserPhotos(@QueryMap Map<String, String> options, Callback<Response> callback);

    @GET("/")
    void loginUser(@QueryMap Map<String, String> params, Callback<Response> callback);
}
