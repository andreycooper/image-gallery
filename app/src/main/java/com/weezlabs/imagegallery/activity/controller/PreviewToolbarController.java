package com.weezlabs.imagegallery.activity.controller;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.weezlabs.imagegallery.R;


public class PreviewToolbarController {
    private AppCompatActivity mActivity;
    private Toolbar mToolbar;

    public PreviewToolbarController(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void create() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            mActivity.setSupportActionBar(mToolbar);
        }

        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setHomeButtonEnabled(true);
            mActivity.getSupportActionBar().setShowHideAnimationEnabled(true);
        }
    }

    public void destroy() {
        mActivity = null;
        mToolbar = null;
    }

    public void setFullscreen(boolean isFullscreen) {
        if (isFullscreen) {
            setFullscreenMode();
        } else {
            setWindowMode();
        }
    }

    private void setFullscreenMode() {
        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideStatusBar();
        }
    }

    private void setWindowMode() {
        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            showStatusBar();
        }
    }

    private void hideStatusBar() {
        View decorView = mActivity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar() {
        View decorView = mActivity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
