package com.weezlabs.imagegallery.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.path.android.jobqueue.JobManager;
import com.weezlabs.imagegallery.ImageGalleryApp;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.job.LoginFlickrUserJob;
import com.weezlabs.imagegallery.model.flickr.Token;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.service.oauth.OnOAuthCallBackListener;
import com.weezlabs.imagegallery.service.oauth.RetrofitHttpOAuthConsumer;
import com.weezlabs.imagegallery.storage.FlickrStorage;

import javax.inject.Inject;

import dagger.Lazy;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FlickrLoginActivity extends AppCompatActivity
        implements OnOAuthCallBackListener, OnPageLoadListener {
    private final static String LOG_TAG = FlickrLoginActivity.class.getSimpleName();

    public static final String ACCESS_TOKEN_URL = "https://www.flickr.com/services/oauth/access_token";
    public static final String USER_AUTHORIZATION_URL = "https://www.flickr.com/services/oauth/authorize";
    public static final String REQUEST_TOKEN_URL = "https://www.flickr.com/services/oauth/request_token";

    private static final String PERMS_READ = "&perms=read";
    private static final String PERMS_WRITE = "&perms=write";
    private static final String PERMS_DELETE = "&perms=delete";

    private WebView mWebView;

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;

    @Inject
    FlickrStorage mFlickrStorage;
    @Inject
    Lazy<FlickrService> mServiceLazy;
    @Inject
    Lazy<JobManager> mJobManagerLazy;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_login);
        Timber.tag(LOG_TAG);

        ImageGalleryApp.get(this).getAppComponent().inject(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // it's Ok to create WebView through App context for Flickr's login
        // but it's crash when login to Twitter
        mWebView = new WebView(getApplicationContext());
        // need JavaScript enabled for Flickr's login page
        mWebView.getSettings().setJavaScriptEnabled(true);
        LinearLayout webViewLayout = (LinearLayout) findViewById(R.id.webview_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.webview_progress_horizontal);
        webViewLayout.addView(mWebView);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
            }
        });
        WebViewClient webViewClient = new OAuthWebViewClient(this, this);
        mWebView.setWebViewClient(webViewClient);

        initFlickr();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFlickr() {
        mConsumer = new RetrofitHttpOAuthConsumer(
                getString(R.string.flickr_consumer_api_key),
                getString(R.string.flickr_consumer_api_secret));

        mProvider = new DefaultOAuthProvider(
                REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL,
                USER_AUTHORIZATION_URL);

        Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            String oauthCallBackUrl = getString(R.string.flickr_oauth_callback_url);
                            String authUrl = mProvider.retrieveRequestToken(mConsumer, oauthCallBackUrl);
                            subscriber.onNext(authUrl);
                        } catch (OAuthMessageSignerException | OAuthNotAuthorizedException
                                | OAuthExpectationFailedException | OAuthCommunicationException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                })
                // append permission to 'https://www.flickr.com/services/oauth/authorize?oauth_token=TOKEN'
                // because Flickr throw error without it
                .map(authUrl -> authUrl + PERMS_READ)
                .filter(authUrl -> !TextUtils.isEmpty(authUrl) || !authUrl.equals(PERMS_READ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        //onNext
                        // its similar to authUrl -> mWebView.loadUrl(authUrl)
                        mWebView::loadUrl,
                        //onError
                        error -> Toast.makeText(getApplicationContext(),
                                getString(R.string.toast_error_login, error.getMessage()),
                                Toast.LENGTH_SHORT)
                                .show());
    }

    private void startLoginJob() {
        FlickrService service = mServiceLazy.get();
        JobManager jobManager = mJobManagerLazy.get();
        jobManager.addJobInBackground(new LoginFlickrUserJob(mFlickrStorage, service));
    }

    @Override
    public void onOAuthCallback(final Uri uri) {
        Observable.create(
                new Observable.OnSubscribe<Token>() {
                    @Override
                    public void call(Subscriber<? super Token> subscriber) {
                        String pinCode = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                        try {
                            mProvider.retrieveAccessToken(mConsumer, pinCode);
                            Token token = new Token(mConsumer.getToken(), mConsumer.getTokenSecret());
                            subscriber.onNext(token);
                        } catch (OAuthMessageSignerException | OAuthNotAuthorizedException
                                | OAuthExpectationFailedException | OAuthCommunicationException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        //onNext
                        // its similar to token -> mFlickrStorage.saveToken(token)
                        mFlickrStorage::saveToken,
                        //onError
                        error -> Toast.makeText(getApplicationContext(),
                                getString(R.string.toast_error_login, error.getMessage()),
                                Toast.LENGTH_SHORT)
                                .show(),
                        //onComplete
                        () -> {
                            if (mFlickrStorage.isAuthenticated()) {
                                startLoginJob();
                                finish();
                            }
                        });
    }

    @Override
    public void onPageStarted() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
    }

    @Override
    public void onPageFinished() {
        mProgressBar.setProgress(getResources().getInteger(R.integer.webview_progress_max));
        mProgressBar.setVisibility(View.GONE);
    }

    private static class OAuthWebViewClient extends WebViewClient {
        OnOAuthCallBackListener mCallBackListener;
        OnPageLoadListener mPageLoadListener;

        public OAuthWebViewClient(OnOAuthCallBackListener listener,
                                  OnPageLoadListener pageLoadListener) {
            mCallBackListener = listener;
            mPageLoadListener = pageLoadListener;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mPageLoadListener.onPageStarted();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mPageLoadListener.onPageFinished();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String scheme = view.getContext().getString(R.string.flickr_oauth_scheme);
            if (uri.getScheme().equals(scheme)) {
                mCallBackListener.onOAuthCallback(uri);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
