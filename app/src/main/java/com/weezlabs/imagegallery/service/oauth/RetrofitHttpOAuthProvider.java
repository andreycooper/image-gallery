package com.weezlabs.imagegallery.service.oauth;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;
import okio.BufferedSink;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;


public class RetrofitHttpOAuthProvider extends AbstractOAuthProvider {
    private static final long serialVersionUID = 1L;

    private transient OkHttpClient mHttpClient;

    public RetrofitHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        mHttpClient = new OkHttpClient();
    }

    @Override
    protected HttpRequest createRequest(String endpointUrl) throws Exception {
        Request request = new Request("POST", endpointUrl, null, null);
        return new RetrofitHttpRequestAdapter(request);
    }

    @Override
    protected HttpResponse sendRequest(HttpRequest request) throws Exception {
        com.squareup.okhttp.Request okRequest = buildOkHttpRequest((Request) request.unwrap());
        Response okResponse = mHttpClient.newCall(okRequest).execute();
        retrofit.client.Response response = buildRetrofitResponse(okResponse);
        return new RetrofitHttpResponseAdapter(response);
    }

    private retrofit.client.Response buildRetrofitResponse(Response okResponse) {
        TypedInput inputBody = new ResponseBodyWrapper(okResponse.body());
        retrofit.client.Response response =
                new retrofit.client.Response(
                        okResponse.request().urlString(),
                        okResponse.code(),
                        okResponse.message(),
                        getHeaders(okResponse.headers()),
                        inputBody);
        return response;
    }

    private com.squareup.okhttp.Request buildOkHttpRequest(retrofit.client.Request request) {
        com.squareup.okhttp.Request.Builder requestBuilder = new com.squareup.okhttp.Request.Builder();
        // — -copy headers — -
        List<Header> headers = request.getHeaders();
        Headers.Builder okHeadersBuilder = new Headers.Builder();
        if (headers != null && headers.size() > 0) {
            for (Header header : headers) {
                okHeadersBuilder.add(header.getName(), header.getValue());
            }
            requestBuilder.headers(okHeadersBuilder.build());
        }
        // — — copy url — —
        requestBuilder.url(request.getUrl());
        // — — copy method and create request body — -
        if (!request.getMethod().equalsIgnoreCase("GET")) {
            requestBuilder.method(request.getMethod(), new RequestBodyWrapper(request.getBody()));
        } else {
            requestBuilder.get();
        }
        return requestBuilder.build();
    }

    private List<Header> getHeaders(Headers headers) {
        List<Header> retrofitHeaders = new ArrayList<Header>();
        int headerCount = headers.names().size();
        for (int i = 0; i < headerCount; i++) {
            retrofitHeaders.add(new Header(headers.name(i), headers.value(i)));
        }
        return retrofitHeaders;
    }

    public static class RequestBodyWrapper extends RequestBody {
        private final TypedOutput mWrapped;

        public RequestBodyWrapper(TypedOutput output) {
            mWrapped = output;
        }

        @Override
        public long contentLength() {
            return mWrapped.length();
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(mWrapped.mimeType());
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            mWrapped.writeTo(sink.outputStream());
        }
    }

    public static class ResponseBodyWrapper implements TypedInput {
        private ResponseBody mWrapped;

        public ResponseBodyWrapper(ResponseBody body) {
            mWrapped = body;
        }

        @Override
        public String mimeType() {
            return mWrapped.contentType().type();
        }

        @Override
        public long length() {
            try {
                return mWrapped.contentLength();
            } catch (IOException e) {
                return -1L;
            }
        }

        @Override
        public InputStream in() throws IOException {
            return mWrapped.byteStream();
        }
    }


}
