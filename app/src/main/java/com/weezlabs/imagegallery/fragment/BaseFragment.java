package com.weezlabs.imagegallery.fragment;

import android.app.Fragment;
import android.net.Uri;

/**
 * Created by Andrey Bondarenko on 03.06.15.
 */
public abstract class BaseFragment extends Fragment {

    public enum ViewMode {
        LIST_MODE(0),
        GRID_MODE(1),
        STAGGERED_MODE(2);

        private int mMode;

        private ViewMode(int modeValue) {
            mMode = modeValue;
        }

        public int getMode() {
            return mMode;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
