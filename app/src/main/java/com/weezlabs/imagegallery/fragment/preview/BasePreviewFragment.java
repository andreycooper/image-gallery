package com.weezlabs.imagegallery.fragment.preview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.util.FileUtils;
import com.weezlabs.imagegallery.util.ImageFactory;

import de.greenrobot.event.EventBus;
import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

import static com.weezlabs.imagegallery.util.ImageFactory.IMAGE;


public abstract class BasePreviewFragment extends Fragment {

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
        mProgressBar.setIndeterminateDrawable(new IndeterminateProgressDrawable(getActivity()));

        imageView.setOnClickListener(v -> EventBus.getDefault().post(new Events.ToolbarVisibilityEvent()));

        loadImageIntoView(imageView);

        return rootView;
    }

    protected abstract void loadImageIntoView(View imageView);

    protected abstract int getLayoutId();

}
