package com.weezlabs.imagegallery.model;


public interface Image {
    int getHeight();

    int getWidth();

    String getTitle();

    String getPath();

    String getOriginalPath();

    String getMimeType();

    long getBucketId();

    long getTakenDate();

    long getSize();
}
