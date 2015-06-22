package com.weezlabs.imagegallery.fragment;

import android.app.Fragment;
import android.os.Bundle;

public abstract class BackHandledFragment extends Fragment {
    protected BackHandlerInterface mBackHandlerInterface;

    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            mBackHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mark this fragment as the selected Fragment.
        mBackHandlerInterface.setSelectedFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBackHandlerInterface = null;
    }

    public interface BackHandlerInterface {
        void setSelectedFragment(BackHandledFragment backHandledFragment);

        void setTitle(String title);

        void setBackArrow();

        void setHamburgerIcon();
    }
}