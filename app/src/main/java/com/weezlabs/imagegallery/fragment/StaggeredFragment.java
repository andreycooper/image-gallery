package com.weezlabs.imagegallery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.weezlabs.imagegallery.FolderAdapter;
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
        mFolderAdapter = new FolderAdapter(getActivity(), null, R.layout.item_folder_grid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_staggered, container, false);
        mListView = (StaggeredGridView) rootView.findViewById(R.id.staggered_view);
        mListView.setAdapter(mFolderAdapter);
        return rootView;
    }

}
