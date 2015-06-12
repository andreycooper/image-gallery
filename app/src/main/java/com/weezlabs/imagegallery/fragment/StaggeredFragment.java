package com.weezlabs.imagegallery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.weezlabs.imagegallery.FolderCursorAdapter;
import com.weezlabs.imagegallery.R;


public class StaggeredFragment extends BaseFragment {

    public static StaggeredFragment newInstance() {
        StaggeredFragment fragment = new StaggeredFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StaggeredFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // get parameters there if need
        }
        mFolderCursorAdapter = new FolderCursorAdapter(getActivity(), null, R.layout.item_grid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_staggered, container, false);
        StaggeredGridView staggeredGridView = (StaggeredGridView) rootView.findViewById(R.id.staggered_view);
        staggeredGridView.setAdapter(mFolderCursorAdapter);
        return rootView;
    }

}
