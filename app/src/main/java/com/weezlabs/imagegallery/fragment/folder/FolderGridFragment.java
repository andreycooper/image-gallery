package com.weezlabs.imagegallery.fragment.folder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.adapter.FolderAdapter;
import com.weezlabs.imagegallery.fragment.image.ImageGridFragment;


public class FolderGridFragment extends BaseFolderFragment {

    public static FolderGridFragment newInstance() {
        FolderGridFragment fragment = new FolderGridFragment();
        return fragment;
    }

    public FolderGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderAdapter = new FolderAdapter(getActivity(), null, R.layout.item_folder_grid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_grid, container, false);
        mListView = (GridView) rootView.findViewById(R.id.grid_view);
        mListView.setAdapter(mFolderAdapter);
        mListView.setOnItemClickListener(new OnFolderItemClickListener());
        return rootView;
    }

    @Override
    protected Fragment getImageFragment(long bucketId) {
        return ImageGridFragment.newInstance(bucketId);
    }

}
