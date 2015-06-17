package com.weezlabs.imagegallery.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.activity.controller.PreviewToolbarController;
import com.weezlabs.imagegallery.R;


public class PreviewActivity extends AppCompatActivity {

    private boolean mIsFullscreen = false;
    private PreviewToolbarController mToolbarController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);

        mToolbarController = new PreviewToolbarController(this);
        mToolbarController.create();

        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        Glide.with(this)
                .load(R.mipmap.ic_launcher)
                .centerCrop()
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFullscreen = !mIsFullscreen;
                mToolbarController.setFullscreen(mIsFullscreen);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mToolbarController.destroy();
    }
}
