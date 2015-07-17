package com.weezlabs.imagegallery.model.flickr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PhotosResponse {
    @Expose
    @SerializedName("photos")
    private Photos mPhotos;
    @Expose
    @SerializedName("stat")
    private String mStatus;
}
