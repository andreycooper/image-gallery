package com.weezlabs.imagegallery.fragment.preview;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.util.FileUtils;
import com.weezlabs.imagegallery.util.ImageFactory;

import de.greenrobot.event.EventBus;
import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

import static com.weezlabs.imagegallery.util.ImageFactory.IMAGE;


public abstract class BasePreviewFragment extends Fragment {

    public static final String BUNDLE_STATE = "fragment.preview.ImageViewState";

    protected Image mImage;
    protected ProgressBar mProgressBar;

    public static BasePreviewFragment newInstance(Image image) {
        BasePreviewFragment fragment;
        if (image.getMimeType().equals(FileUtils.IMAGE_TYPE_GIF)) {
            fragment = new PreviewGifFragment();
        } else {
            fragment = new PreviewImageFragment();
        }
        Bundle args = ImageFactory.buildFragmentArguments(image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mImage = getArguments().getParcelable(IMAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        View imageView = rootView.findViewById(R.id.image_view);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.indeterminate_progress);
        setupProgressBar(mProgressBar);

        imageView.setOnClickListener(v -> EventBus.getDefault().post(new Events.ToolbarVisibilityEvent()));

        ImageViewState imageViewState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
            imageViewState = (ImageViewState) savedInstanceState.getSerializable(BUNDLE_STATE);
        }

        loadImageIntoView(imageView, imageViewState);

        return rootView;
    }

    private void setupProgressBar(ProgressBar progressBar) {
        IndeterminateProgressDrawable progressDrawable = new IndeterminateProgressDrawable(getActivity());
        int progressColor = getResources().getColor(R.color.material_drawer_primary_dark);
        progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(progressDrawable);
    }

    protected abstract void loadImageIntoView(View imageView, ImageViewState imageViewState);

    protected abstract int getLayoutId();

}
