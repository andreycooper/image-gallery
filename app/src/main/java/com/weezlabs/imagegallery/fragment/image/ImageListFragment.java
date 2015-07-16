package com.weezlabs.imagegallery.fragment.image;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.twotoasters.sectioncursoradapter.adapter.SectionCursorAdapter;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.PreviewActivity;
import com.weezlabs.imagegallery.adapter.SectionListImageAdapter;
import com.weezlabs.imagegallery.model.local.Image;

import timber.log.Timber;

public class ImageListFragment extends BaseImageFragment {

    private static final String LOG_TAG = ImageListFragment.class.getSimpleName();

    public static ImageListFragment newInstance(long bucketId) {
        ImageListFragment fragment = new ImageListFragment();
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, bucketId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(LOG_TAG);
        mImageAdapter = new SectionListImageAdapter(getActivity(), null);
        loadImages();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(mImageAdapter);
        mListView.setOnItemClickListener(new OnImageItemClickListener());
        return rootView;
    }

    @Override
    protected Intent getPreviewIntent(AdapterView<?> parent, View view, int position, long id) {
        Timber.d("click in SectionListImageAdapter, position: %s,  id: %s", position, id);

        SectionListImageAdapter adapter = (SectionListImageAdapter) mListView.getAdapter();
        Object sectionObject = adapter.getItem(position);
        int cursorPosition = adapter.getCursorPositionWithoutSections(position);

        if (adapter.isSection(position) && sectionObject != null) {
            // Handle the section being clicked on.
            Timber.d("Section: %s", sectionObject.toString());
        } else if (cursorPosition != SectionCursorAdapter.NO_CURSOR_POSITION) {
            // Handle the cursor item being clicked on.
            Image image = adapter.getImage(position);
            Intent intent = new Intent(getActivity(), PreviewActivity.class);
            intent.putExtra(PreviewActivity.EXTRA_IMAGE_POSITION, cursorPosition);
            intent.putExtra(PreviewActivity.EXTRA_BUCKET_ID, image.getBucketId());
            getActivity().startActivity(intent);
        }
        return null;
    }
}
