package com.weezlabs.imagegallery.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.fragment.folder.FolderGridFragment;
import com.weezlabs.imagegallery.fragment.folder.FolderListFragment;
import com.weezlabs.imagegallery.fragment.folder.FolderStaggeredFragment;
import com.weezlabs.imagegallery.util.Utils;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    protected static final String EXTRA_ACCOUNT_NAME = "account_name";

    protected String mAccountName;
    protected Menu mMenu;

    /**
     * Called on activity creation. Handlers {@code EXTRA_ACCOUNT_NAME} for
     * handle if there is one set. Otherwise, looks for the first Google account
     * on the device and automatically picks it for client connections.
     */
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (b != null) {
            mAccountName = b.getString(EXTRA_ACCOUNT_NAME);
        }
        if (mAccountName == null) {
            mAccountName = getIntent().getStringExtra(EXTRA_ACCOUNT_NAME);
        }

        if (mAccountName == null) {
            Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
            if (accounts.length == 0) {
                Log.d(LOG_TAG, "Must have a Google account installed");
                return;
            } else {
                for (Account account : accounts) {
                    Log.d(LOG_TAG, account.toString());
                }
            }
            mAccountName = accounts[0].name;
        }
    }

    /**
     * Saves the activity state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_ACCOUNT_NAME, mAccountName);
    }

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
