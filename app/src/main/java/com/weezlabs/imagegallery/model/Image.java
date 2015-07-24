package com.weezlabs.imagegallery.model;


public interface Image {
    int getHeight();

    int getWidth();

    String getPath();

    String getOriginalPath();

    String getMimeType();
}
