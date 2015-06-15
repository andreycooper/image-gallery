package com.weezlabs.imagegallery;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.weezlabs.imagegallery.fragment.BackHandledFragment;
import com.weezlabs.imagegallery.fragment.BackHandledFragment.BackHandlerInterface;
import com.weezlabs.imagegallery.util.Utils;


public class MainActivity extends BaseActivity implements BackHandlerInterface {
    private BackHandledFragment mBackHandledFragment;
    private Drawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewMode viewMode = Utils.getViewMode(this);
        setupModeFragment(viewMode);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        //pass your items here
                        new PrimaryDrawerItem().withName(R.string.label_drawer_list).withIcon(R.drawable.ic_mode_list),
                        new PrimaryDrawerItem().withName(R.string.label_drawer_grid).withIcon(R.drawable.ic_mode_grid),
                        new PrimaryDrawerItem().withName(R.string.label_drawer_staggered).withIcon(R.drawable.ic_mode_staggered),
                        new DividerDrawerItem()
                )
                .build();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
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
    public void setBackArrow() {
        if (getSupportActionBar() != null) {
            mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setHamurgerIcon() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }
    }
}
