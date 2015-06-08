package com.weezlabs.imagegallery.fragment;

import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.weezlabs.imagegallery.FolderCursorAdapter;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Folder;


public class ListFragment extends BaseFragment {

    private static final String LOG_TAG = ListFragment.class.getSimpleName();

    private ListView mFolderListView;

    private FolderCursorAdapter mFolderCursorAdapter;

    private OnFragmentInteractionListener mListener;

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

        loadFoldersCursor();
        mFolderCursorAdapter = new FolderCursorAdapter(getActivity(), null, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mFolderListView = (ListView) rootView.findViewById(R.id.list_view);
        mFolderListView.setAdapter(mFolderCursorAdapter);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FOLDERS_LOADER:
                // TODO: delete temp implementation
//                logCursor(cursor);
                mFolderCursorAdapter.changeCursor(cursor);
                break;
            case IMAGES_LOADER:
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FOLDERS_LOADER:
                mFolderCursorAdapter.changeCursor(null);
                break;
            case IMAGES_LOADER:
                break;
            default:
                break;
        }
    }

    private void logCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            Log.i(LOG_TAG, "Folders with images:");
            Folder folder;
            do {
                folder = new Folder(cursor);
                Log.i(LOG_TAG, folder.toString());
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}
