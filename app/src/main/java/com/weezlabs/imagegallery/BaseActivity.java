package com.weezlabs.imagegallery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.weezlabs.imagegallery.fragment.GridFragment;
import com.weezlabs.imagegallery.fragment.ListFragment;
import com.weezlabs.imagegallery.fragment.StaggeredFragment;
import com.weezlabs.imagegallery.util.Utils;


public abstract class BaseActivity extends AppCompatActivity {

    protected void changeViewMode(MenuItem item) {
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
        setupModeIcon(item, viewMode);
    }

    protected void setupModeFragment(ViewMode viewMode) {
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

    protected void setupModeIcon(MenuItem item, ViewMode viewMode) {
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

    protected void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public enum ViewMode {
        LIST_MODE(0),
        GRID_MODE(1),
        STAGGERED_MODE(2);

        private int mMode;

        ViewMode(int modeValue) {
            mMode = modeValue;
        }

        public int getMode() {
            return mMode;
        }
    }
}
