package com.weezlabs.imagegallery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.weezlabs.imagegallery.FolderCursorAdapter;
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
        mFolderCursorAdapter = new FolderCursorAdapter(getActivity(), null, R.layout.item_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ListView folderListView = (ListView) rootView.findViewById(R.id.list_view);
        folderListView.setAdapter(mFolderCursorAdapter);
        return rootView;
    }

}
