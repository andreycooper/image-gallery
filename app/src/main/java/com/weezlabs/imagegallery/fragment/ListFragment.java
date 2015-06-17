package com.weezlabs.imagegallery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.weezlabs.imagegallery.adapter.FolderAdapter;
import com.weezlabs.imagegallery.adapter.ImageAdapter;
import com.weezlabs.imagegallery.R;


public class ListFragment extends BaseFragment {

    private static final String LOG_TAG = ListFragment.class.getSimpleName();

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // get parameters there if need
        }
        mFolderAdapter = new FolderAdapter(getActivity(), null, R.layout.item_folder_list);
        mImageAdapter = new ImageAdapter(getActivity(), null, R.layout.item_image_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(mFolderAdapter);
        mListView.setOnItemClickListener(new OnGalleryItemClickListener());
        return rootView;
    }

}
