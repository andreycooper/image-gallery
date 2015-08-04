package com.weezlabs.imagegallery.service.oauth;


import android.net.Uri;

public interface OnOAuthCallBackListener {
    void onOAuthCallback(Uri uri);
}
