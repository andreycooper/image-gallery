package com.weezlabs.imagegallery.activity.controller;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.weezlabs.imagegallery.R;

import java.lang.ref.WeakReference;


public class PreviewToolbarController {
    private final WeakReference<AppCompatActivity> mActivityWeakReference;
    private Toolbar mToolbar;

    public PreviewToolbarController(AppCompatActivity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    public void create() {
        AppCompatActivity activity = getActivity();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mToolbar = (Toolbar) activity.findViewById(R.id.toolbar);
            activity.setSupportActionBar(mToolbar);
        }

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
            activity.getSupportActionBar().setShowHideAnimationEnabled(true);
            activity.getSupportActionBar().setTitle("");
        }
    }

    public void destroy() {
        mActivityWeakReference.clear();
        mToolbar = null;
    }

    public void setTitle(String title) {
        AppCompatActivity activity = getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

    public void setFullscreen(boolean isFullscreen) {
        if (isFullscreen) {
            setFullscreenMode();
        } else {
            setWindowMode();
        }
    }

    private void setFullscreenMode() {
        AppCompatActivity activity = getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideStatusBar();
        }
    }

    private void setWindowMode() {
        if (getActivity().getSupportActionBar() != null) {
            getActivity().getSupportActionBar().show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            showStatusBar();
        }
    }

    private void hideStatusBar() {
        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar() {
        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private AppCompatActivity getActivity() {
        return mActivityWeakReference.get();
    }
}
