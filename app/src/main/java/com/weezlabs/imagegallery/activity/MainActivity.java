package com.weezlabs.imagegallery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.fragment.BackHandledFragment;
import com.weezlabs.imagegallery.fragment.BackHandledFragment.BackHandlerInterface;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.util.FlickrUtils;
import com.weezlabs.imagegallery.util.Utils;


public class MainActivity extends BaseActivity implements BackHandlerInterface {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
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

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withSavedInstance(savedInstanceState)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.drawer_header_background)
                .build();
        mDrawer = getDrawer(accountHeader, savedInstanceState);
        mDrawer.setSelection(getDrawerSelectedMode(), false);
    }

    private Drawer getDrawer(AccountHeader accountHeader, Bundle savedInstanceState) {
        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new SectionDrawerItem()
                                .withName(getString(R.string.label_drawer_section_view_mode))
                                .setDivider(false),
                        new PrimaryDrawerItem()
                                .withName(R.string.label_drawer_list)
                                .withIcon(GoogleMaterial.Icon.gmd_view_list)
                                .withIdentifier(ViewMode.LIST_MODE.getMode()),
                        new PrimaryDrawerItem()
                                .withName(R.string.label_drawer_grid)
                                .withIcon(GoogleMaterial.Icon.gmd_view_module)
                                .withIdentifier(ViewMode.GRID_MODE.getMode()),
                        new PrimaryDrawerItem()
                                .withName(R.string.label_drawer_staggered)
                                .withIcon(GoogleMaterial.Icon.gmd_view_quilt)
                                .withIdentifier(ViewMode.STAGGERED_MODE.getMode()),
                        new DividerDrawerItem()
                )
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        onBackPressed();
                        return true;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (isViewModeDrawerItem(drawerItem)) {
                                changeViewMode(drawerItem.getIdentifier());
                                mDrawer.closeDrawer();
                            }
                        }
                        return true;
                    }
                })
                .build();
    }

    private boolean isViewModeDrawerItem(IDrawerItem drawerItem) {
        return drawerItem.getIdentifier() >= ViewMode.LIST_MODE.getMode()
                && drawerItem.getIdentifier() <= ViewMode.STAGGERED_MODE.getMode();
    }

    private int getDrawerSelectedMode() {
        int viewMode = Utils.getViewMode(MainActivity.this).getMode();
        for (int i = 0; i < mDrawer.getDrawerItems().size(); i++) {
            if (viewMode == mDrawer.getDrawerItems().get(i).getIdentifier()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
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
                test();
                return true;
            case R.id.action_change_mode:
                swapViewMode(item);
                mDrawer.setSelection(getDrawerSelectedMode(), false);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void test() {
        if (Utils.isOnline(this)) {
            if (!FlickrUtils.isAuthenticated(getApplicationContext())) {
                Intent intent = new Intent(this, FlickrLoginActivity.class);
                startActivity(intent);
            } else {
                User user = FlickrUtils.restoreFlickrUser(getApplicationContext());
                if (user != null) {
                    Toast.makeText(this, "Flickr user: " + user.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_internet_check),
                    Toast.LENGTH_SHORT)
                    .show();
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
