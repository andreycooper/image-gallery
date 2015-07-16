package com.weezlabs.imagegallery.fragment.image;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.activity.PreviewActivity;
import com.weezlabs.imagegallery.adapter.ImageAdapter;
import com.weezlabs.imagegallery.adapter.SectionGridImageAdapter;
import com.weezlabs.imagegallery.model.local.Image;

import timber.log.Timber;

public class ImageGridFragment extends BaseImageFragment {

    private static final String LOG_TAG = ImageGridFragment.class.getSimpleName();

    public static ImageGridFragment newInstance(long bucketId) {
        ImageGridFragment fragment = new ImageGridFragment();
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, bucketId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(LOG_TAG);
        mImageAdapter = new SectionGridImageAdapter(getActivity(), null);
        loadImages();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_grid, container, false);
        mListView = (StickyGridHeadersGridView) rootView.findViewById(R.id.grid_view);
        ((StickyGridHeadersGridView) mListView).setAreHeadersSticky(false);
        mListView.setAdapter(mImageAdapter);
        mListView.setOnItemClickListener(new OnImageItemClickListener());
        return rootView;
    }

    @Override
    protected Intent getPreviewIntent(AdapterView<?> parent, View view, int position, long id) {
        Timber.d("click in SectionListImageAdapter, position: %s, %s", position, id);

        Image image = ((ImageAdapter) mImageAdapter).getImage(position);
        Intent intent = new Intent(getActivity(), PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_IMAGE_POSITION, position);
        intent.putExtra(PreviewActivity.EXTRA_BUCKET_ID, image.getBucketId());
        return intent;
    }
}
