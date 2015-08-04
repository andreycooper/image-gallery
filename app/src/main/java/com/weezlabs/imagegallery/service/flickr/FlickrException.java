package com.weezlabs.imagegallery.service.flickr;


public class FlickrException extends Exception {
    private String mMessage;

    public FlickrException(String detailMessage) {
        super(detailMessage);
        mMessage = detailMessage;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }
}
