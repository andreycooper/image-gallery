package com.weezlabs.imagegallery.service.oauth;

import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.http.HttpResponse;
import retrofit.client.Response;


public class RetrofitHttpResponseAdapter implements HttpResponse {

    private Response mResponse;

    public RetrofitHttpResponseAdapter(Response response) {
        mResponse = response;
    }

    @Override
    public int getStatusCode() throws IOException {
        return mResponse.getStatus();
    }

    @Override
    public String getReasonPhrase() throws Exception {
        return mResponse.getReason();
    }

    @Override
    public InputStream getContent() throws IOException {
        return mResponse.getBody().in();
    }

    @Override
    public Object unwrap() {
        return mResponse;
    }
}
