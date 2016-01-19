package com.weezlabs.imagegallery.fragment.folder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.view.adapter.FolderAdapter;


public class FolderListFragment extends BaseFolderFragment {

    private static final String LOG_TAG = FolderListFragment.class.getSimpleName();

    public static FolderListFragment newInstance() {
        FolderListFragment fragment = new FolderListFragment();
        return fragment;
    }

    public FolderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderAdapter = new FolderAdapter(getActivity(), null, R.layout.item_folder_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(mFolderAdapter);
        mListView.setOnItemClickListener(new OnFolderItemClickListener());
        return rootView;
    }

}
