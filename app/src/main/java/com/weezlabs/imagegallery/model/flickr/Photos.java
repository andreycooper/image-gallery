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

    public Photos() {
    }

    public int getPage() {
        return mPage;
    }

    public int getPagesCount() {
        return mPagesCount;
    }

    public int getPerPageCount() {
        return mPerPageCount;
    }

    public int getTotalPhotosCount() {
        return mTotalPhotosCount;
    }

    public List<Photo> getPhotoList() {
        return mPhotoList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Photos{");
        sb.append("mPage=").append(mPage);
        sb.append(", mPagesCount=").append(mPagesCount);
        sb.append(", mPerPageCount=").append(mPerPageCount);
        sb.append(", mTotalPhotosCount=").append(mTotalPhotosCount);
        sb.append(", mPhotoList=").append(mPhotoList);
        sb.append('}');
        return sb.toString();
    }
}
