package com.weezlabs.imagegallery.dagger.module;

import android.content.Context;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.service.flickr.FlickrApi;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.service.oauth.RetrofitHttpOAuthConsumer;
import com.weezlabs.imagegallery.service.oauth.SigningOkClient;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    public RetrofitHttpOAuthConsumer provideOAuthConsumer(Context context, FlickrStorage flickrStorage) {
        RetrofitHttpOAuthConsumer oAuthConsumer =
                new RetrofitHttpOAuthConsumer(context.getString(R.string.flickr_consumer_api_key),
                        context.getString(R.string.flickr_consumer_api_secret));
        oAuthConsumer.setTokenWithSecret(flickrStorage.getToken(), flickrStorage.getTokenSecret());
        return oAuthConsumer;
    }

    @Singleton
    @Provides
    public OkClient provideOkClient(RetrofitHttpOAuthConsumer oAuthConsumer) {
        return new SigningOkClient(oAuthConsumer);
    }

    @Singleton
    @Provides
    public Endpoint provideEndpoint(Context context) {
        return Endpoints.newFixedEndpoint(context.getString(R.string.flickr_endpoint_url));
    }

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(Endpoint endpoint, OkClient okClient) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(okClient)
                .build();
    }

    @Singleton
    @Provides
    public FlickrApi provideFlickrApi(RestAdapter restAdapter) {
        return restAdapter.create(FlickrApi.class);
    }

    @Singleton
    @Provides
    public FlickrService provideFlickrService(Context context,
                                              FlickrStorage flickrStorage,
                                              RetrofitHttpOAuthConsumer oAuthConsumer,
                                              FlickrApi api) {
        return new FlickrService(context, flickrStorage, oAuthConsumer, api);
    }
}
