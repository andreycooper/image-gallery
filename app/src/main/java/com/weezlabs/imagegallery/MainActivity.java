package com.weezlabs.imagegallery;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.util.Utils;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewMode viewMode = Utils.getViewMode(this);
        setupModeFragment(viewMode);

        SyncImagesIntentService.startActionGetImagesFromUri(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        SyncImagesIntentService.startActionSyncImages(this, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_change_mode);
        setupModeIcon(item, Utils.getViewMode(this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_change_mode:
                changeViewMode(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
