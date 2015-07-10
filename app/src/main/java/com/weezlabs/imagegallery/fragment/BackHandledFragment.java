package com.weezlabs.imagegallery.fragment;

import android.app.Fragment;
import android.os.Bundle;

import timber.log.Timber;

public abstract class BackHandledFragment extends Fragment {
    protected BackHandlerInterface mBackHandler;

    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            mBackHandler = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mark this fragment as the selected Fragment.
        mBackHandler.setSelectedFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBackHandler = null;
    }

    public interface BackHandlerInterface {
        void setSelectedFragment(BackHandledFragment backHandledFragment);

        void setTitle(String title);

        void setBackArrow();

        void setHamburgerIcon();
    }
}