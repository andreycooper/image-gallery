package com.weezlabs.imagegallery.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.service.flickr.FlickrResponse;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.service.oauth.OAuthTask;
import com.weezlabs.imagegallery.service.oauth.OnOAuthCallBackListener;
import com.weezlabs.imagegallery.service.oauth.RetrofitHttpOAuthConsumer;
import com.weezlabs.imagegallery.util.FlickrUtils;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class FlickrLoginActivity extends AppCompatActivity implements OnOAuthCallBackListener {
    private final static String LOG_TAG = FlickrLoginActivity.class.getSimpleName();

    public static final String ACCESS_TOKEN_URL = "https://www.flickr.com/services/oauth/access_token";
    public static final String USER_AUTHORIZATION_URL = "https://www.flickr.com/services/oauth/authorize";
    public static final String REQUEST_TOKEN_URL = "https://www.flickr.com/services/oauth/request_token";

    private WebView mWebView;
    private String mAuthUrl;

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_login);
        Timber.tag(LOG_TAG);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mWebView = new WebView(this);
        RelativeLayout webViewLayout = (RelativeLayout) findViewById(R.id.webview_layout);
        webViewLayout.addView(mWebView);

        WebViewClient webViewClient = new OAuthWebViewClient(this);
        mWebView.setWebViewClient(webViewClient);

        initFlickr();

        if (TextUtils.isEmpty(mAuthUrl)) {
            mWebView.loadUrl(mAuthUrl);
        }

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
        mProvider.setOAuth10a(true);


        OAuthTask oAuthTask = new OAuthTask(this) {
            @Override
            protected Void doInBackground(Void... params) {
                String oauthCallBackUrl = getString(R.string.flickr_oauth_callback_url);
                try {
                    mAuthUrl = mProvider.retrieveRequestToken(mConsumer, oauthCallBackUrl);
                    Timber.i("mAuthUrl: %s", mAuthUrl);
                } catch (OAuthMessageSignerException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (!TextUtils.isEmpty(mAuthUrl)) {
                    mWebView.loadUrl(mAuthUrl);
                }
            }
        };
        oAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onOAuthCallback(final Uri uri) {
        OAuthTask oAuthTask = new OAuthTask(this) {
            @Override
            protected Void doInBackground(Void... params) {
                String pinCode = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                try {
                    mProvider.retrieveAccessToken(mConsumer, pinCode);

                    String token = mConsumer.getToken();
                    String tokenSecret = mConsumer.getTokenSecret();

                    FlickrUtils.setToken(getApplicationContext(), token);
                    FlickrUtils.setTokenSecret(getApplicationContext(), tokenSecret);


                } catch (OAuthMessageSignerException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    showErrorToUser(e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (FlickrUtils.isAuthenticated(getApplicationContext())) {
                    FlickrService service = new FlickrService(getApplicationContext());
                    service.loginUser(new FlickrResponse<Response>(getApplicationContext()) {
                        @Override
                        public void success(Response response, Response ignored) {
                            String jsonBody =
                                    new String(((TypedByteArray) response.getBody()).getBytes());
                            JsonObject json = new GsonBuilder().create()
                                    .fromJson(jsonBody, JsonObject.class);

                            String userId = json.get("user").getAsJsonObject()
                                    .get("id").getAsString();
                            String username = json.get("user").getAsJsonObject()
                                    .get("username").getAsJsonObject()
                                    .get("_content").getAsString();
                            User user = new User(userId, username);
                            Timber.i("Flickr user: %s", user.toString());
                            FlickrUtils.saveUser(getApplicationContext(), user);
                        }
                    });
                }
                finish();
            }
        };
        oAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class OAuthWebViewClient extends WebViewClient {
        OnOAuthCallBackListener mCallBackListener;

        public OAuthWebViewClient(OnOAuthCallBackListener listener) {
            mCallBackListener = listener;
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
