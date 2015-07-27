package com.weezlabs.imagegallery.service.flickr;


import com.google.gson.JsonObject;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface FlickrApi {
    @GET("/")
    JsonObject executeFlickrRequest(@QueryMap Map<String, String> parameters);

}
