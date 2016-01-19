package com.weezlabs.imagegallery.model.flickr;

/**
 * Created by Andrey Bondarenko on 11.8.15.
 */
public class Token {

    private String mToken;
    private String mTokenSecret;

    public Token() {
        mToken = "";
        mTokenSecret = "";
    }

    public Token(String token, String tokenSecret) {
        mToken = token;
        mTokenSecret = tokenSecret;
    }

    public String getToken() {
        return mToken;
    }

    public String getTokenSecret() {
        return mTokenSecret;
    }
}
