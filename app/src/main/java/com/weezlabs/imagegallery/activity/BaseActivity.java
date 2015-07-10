package com.weezlabs.imagegallery.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.fragment.folder.FolderGridFragment;
import com.weezlabs.imagegallery.fragment.folder.FolderListFragment;
import com.weezlabs.imagegallery.fragment.folder.FolderStaggeredFragment;
import com.weezlabs.imagegallery.util.Utils;


public abstract class BaseActivity extends AppCompatActivity {

    protected Menu mMenu;

    protected void changeViewMode(int viewMode) {
        switch (viewMode) {
            case 0:
                changeViewMode(ViewMode.LIST_MODE);
                break;
            case 1:
                changeViewMode(ViewMode.GRID_MODE);
                break;
            case 2:
                changeViewMode(ViewMode.STAGGERED_MODE);
                break;
            default:
                break;
        }
    }

    protected void changeViewMode(ViewMode viewMode) {
        Utils.setViewMode(this, viewMode);
        setupModeFragment(viewMode);
        setupModeIcon(mMenu.findItem(R.id.action_change_mode), viewMode);
    }

    protected void swapViewMode(MenuItem item) {
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
                fragment = FolderListFragment.newInstance();
                break;
            case GRID_MODE:
                fragment = FolderGridFragment.newInstance();
                break;
            case STAGGERED_MODE:
                fragment = FolderStaggeredFragment.newInstance();
                break;
            default:
                fragment = FolderListFragment.newInstance();
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
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
