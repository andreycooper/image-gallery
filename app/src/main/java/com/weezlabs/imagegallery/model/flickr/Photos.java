package com.weezlabs.imagegallery.model.flickr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Photos {
    @Expose
    @SerializedName("page")
    private int mPage;
    @Expose
    @SerializedName("pages")
    private int mPagesCount;
    @Expose
    @SerializedName("perpage")
    private int mPerPageCount;
    @Expose
    @SerializedName("total")
    private int mTotalPhotosCount;
    @Expose
    @SerializedName("photo")
    private List<Photo> mPhotoList;
}
