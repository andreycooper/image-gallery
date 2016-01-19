package com.weezlabs.imagegallery.fragment.folder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.view.adapter.FolderAdapter;


public class FolderStaggeredFragment extends BaseFolderFragment {

    public static FolderStaggeredFragment newInstance() {
        FolderStaggeredFragment fragment = new FolderStaggeredFragment();
        return fragment;
    }

    public FolderStaggeredFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderAdapter = new FolderAdapter(getActivity(), null, R.layout.item_folder_staggered);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_staggered, container, false);
        mListView = (StaggeredGridView) rootView.findViewById(R.id.staggered_view);
        mListView.setAdapter(mFolderAdapter);
        mListView.setOnItemClickListener(new OnFolderItemClickListener());
        return rootView;
    }

}
