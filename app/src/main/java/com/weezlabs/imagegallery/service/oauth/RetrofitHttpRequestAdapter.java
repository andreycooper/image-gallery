package com.weezlabs.imagegallery.service.oauth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.http.HttpRequest;
import retrofit.client.Header;
import retrofit.client.Request;

public class RetrofitHttpRequestAdapter implements HttpRequest {

    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    private Request mRequest;

    private String mContentType;

    public RetrofitHttpRequestAdapter(Request request) {
        this(request, request.getBody() != null ? request.getBody().mimeType() : DEFAULT_CONTENT_TYPE);
    }

    public RetrofitHttpRequestAdapter(Request request, String contentType) {
        this.mRequest = request;
        this.mContentType = contentType;
    }

    @Override
    public Map<String, String> getAllHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        for (Header header : mRequest.getHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    @Override
    public String getHeader(String key) {
        for (Header header : mRequest.getHeaders()) {
            if (key.equals(header.getName())) {
                return header.getValue();
            }
        }
        return null;
    }

    @Override
    public InputStream getMessagePayload() throws IOException {
        final String contentType = getContentType();
        if (null != contentType && contentType.startsWith("application/x-www-form-urlencoded")) {
            long contentLength = mRequest.getBody().length();
            ByteArrayOutputStream output = new ByteArrayOutputStream(Long.valueOf(contentLength)
                    .intValue());
            mRequest.getBody().writeTo(output);
            return new ByteArrayInputStream(output.toByteArray());
        }

        throw new UnsupportedOperationException("The content type" + (contentType != null ? " " +
                contentType : "") + " is not supported.");
    }

    @Override
    public String getMethod() {
        return mRequest.getMethod();
    }

    @Override
    public String getRequestUrl() {
        return mRequest.getUrl();
    }

    @Override
    public void setHeader(String key, String value) {
        ArrayList<Header> headers = new ArrayList<Header>();
        headers.addAll(mRequest.getHeaders());
        headers.add(new Header(key, value));
        Request copy = new Request(mRequest.getMethod(), mRequest.getUrl(), headers, mRequest.getBody());
        mRequest = copy;
    }

    @Override
    public void setRequestUrl(String url) {
        Request copy = new Request(mRequest.getMethod(), url, mRequest.getHeaders(), mRequest.getBody());
        mRequest = copy;
    }

    @Override
    public Object unwrap() {
        return mRequest;
    }

}

