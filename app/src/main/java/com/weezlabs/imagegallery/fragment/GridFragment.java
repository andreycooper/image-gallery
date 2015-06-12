package com.weezlabs.imagegallery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.weezlabs.imagegallery.FolderCursorAdapter;
import com.weezlabs.imagegallery.R;


public class GridFragment extends BaseFragment {

    public static GridFragment newInstance() {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GridFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
        GridView folderGridView = (GridView) rootView.findViewById(R.id.grid_view);
        folderGridView.setAdapter(mFolderCursorAdapter);
        return rootView;
    }

}
