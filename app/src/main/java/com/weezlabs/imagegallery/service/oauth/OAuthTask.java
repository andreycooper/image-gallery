package com.weezlabs.imagegallery.service.oauth;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.weezlabs.imagegallery.R;

import dmax.dialog.SpotsDialog;
import oauth.signpost.exception.OAuthException;


public abstract class OAuthTask extends AsyncTask<Void, Void, Void> {
    private AlertDialog mDialog;
    private Context mContext;
    private Handler mHandler;


    public OAuthTask(Context context) {
        mContext = context.getApplicationContext();
        mDialog = new SpotsDialog(context);
        mHandler = new Handler(context.getMainLooper());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDialog.dismiss();
    }

    protected void showErrorToUser(final OAuthException e) {
        runOnUiThread(() -> Toast.makeText(mContext,
                mContext.getString(R.string.toast_error_login, e.getMessage()),
                Toast.LENGTH_SHORT)
                .show());
    }

    protected void showErrorToUser(final Exception e) {
        runOnUiThread(() -> Toast.makeText(mContext,
                mContext.getString(R.string.toast_error_login, e.getMessage()),
                Toast.LENGTH_SHORT)
                .show());
    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

}
