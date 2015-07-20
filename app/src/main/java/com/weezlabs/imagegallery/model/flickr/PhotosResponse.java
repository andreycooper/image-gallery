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

    public PhotosResponse() {
    }

    public Photos getPhotos() {
        return mPhotos;
    }

    public String getStatus() {
        return mStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PhotosResponse{");
        sb.append("mPhotos=").append(mPhotos);
        sb.append(", mStatus='").append(mStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
