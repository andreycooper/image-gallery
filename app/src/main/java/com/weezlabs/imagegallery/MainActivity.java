package com.weezlabs.imagegallery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.fragment.BaseFragment.ViewMode;
import com.weezlabs.imagegallery.fragment.GridFragment;
import com.weezlabs.imagegallery.fragment.ListFragment;
import com.weezlabs.imagegallery.fragment.StaggeredFragment;


public class MainActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewMode viewMode = Utils.getViewMode(this);
        setupModeFragment(viewMode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_change_mode);
        setupChangeModeIcon(item, Utils.getViewMode(this));
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

    private void changeViewMode(MenuItem item) {
        ViewMode viewMode;
        switch (Utils.getViewMode(this)) {
            case LIST_MODE:
                viewMode = ViewMode.GRID_MODE;
                break;
            case GRID_MODE:
                viewMode = ViewMode.STAGGERED_MODE;
                break;
            case STAGGERED_MODE:
                viewMode = ViewMode.LIST_MODE;
                break;
            default:
                viewMode = ViewMode.LIST_MODE;
                break;
        }
        Utils.setViewMode(this, viewMode);
        setupModeFragment(viewMode);
        setupChangeModeIcon(item, viewMode);
    }

    private void setupModeFragment(ViewMode viewMode) {
        Fragment fragment;
        switch (viewMode) {
            case LIST_MODE:
                fragment = ListFragment.newInstance();
                break;
            case GRID_MODE:
                fragment = GridFragment.newInstance();
                break;
            case STAGGERED_MODE:
                fragment = StaggeredFragment.newInstance();
                break;
            default:
                fragment = ListFragment.newInstance();
                break;
        }
        replaceFragment(fragment);
    }

    private void setupChangeModeIcon(MenuItem item, ViewMode viewMode) {
        int iconId;
        switch (viewMode) {
            case LIST_MODE:
                iconId = R.drawable.ic_mode_list;
                break;
            case GRID_MODE:
                iconId = R.drawable.ic_mode_grid;
                break;
            case STAGGERED_MODE:
                iconId = R.drawable.ic_mode_staggered;
                break;
            default:
                iconId = R.drawable.ic_mode_list;
                break;
        }
        item.setIcon(iconId);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
