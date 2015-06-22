package com.weezlabs.imagegallery.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.fragment.BackHandledFragment;
import com.weezlabs.imagegallery.fragment.BackHandledFragment.BackHandlerInterface;
import com.weezlabs.imagegallery.util.Utils;


public class MainActivity extends BaseActivity implements BackHandlerInterface {
    private BackHandledFragment mBackHandledFragment;
    private Drawer mDrawer;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ViewMode viewMode = Utils.getViewMode(this);
        setupModeFragment(viewMode);

        mDrawer = getDrawer(savedInstanceState);
    }

    private Drawer getDrawer(Bundle savedInstanceState) {
        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.label_drawer_list)
                                .withIcon(GoogleMaterial.Icon.gmd_view_list),
                        new PrimaryDrawerItem().withName(R.string.label_drawer_grid)
                                .withIcon(GoogleMaterial.Icon.gmd_view_module),
                        new PrimaryDrawerItem().withName(R.string.label_drawer_staggered)
                                .withIcon(GoogleMaterial.Icon.gmd_view_quilt),
                        new DividerDrawerItem()
                )
                .withSavedInstance(savedInstanceState)
                .build();
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
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            if (mBackHandledFragment == null || !mBackHandledFragment.onBackPressed()) {
                // Selected fragment did not consume the back press event.
                super.onBackPressed();
            }
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandledFragment = selectedFragment;
    }

    @Override
    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void setBackArrow() {
        if (getSupportActionBar() != null) {
            mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setHamburgerIcon() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }
    }
}
