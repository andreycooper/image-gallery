package com.weezlabs.imagegallery.fragment.preview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.local.Image;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.util.FileUtils;

import de.greenrobot.event.EventBus;


public abstract class BasePreviewFragment extends Fragment {
    protected static final String IMAGE = "com.weezlabs.imagegallery.IMAGE";

    protected Image mImage;

    public static BasePreviewFragment newInstance(Image image) {
        BasePreviewFragment fragment;
        if (image.getMimeType().equals(FileUtils.IMAGE_TYPE_GIF)) {
            fragment = new PreviewGifFragment();
        } else {
            fragment = new PreviewImageFragment();
        }
        Bundle args = new Bundle();
        args.putParcelable(IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImage = getArguments().getParcelable(IMAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        View imageView = rootView.findViewById(R.id.image_view);

        imageView.setOnClickListener(v -> EventBus.getDefault().post(new Events.ToolbarVisibilityEvent()));

        loadImageIntoView(imageView);

        return rootView;
    }

    protected abstract void loadImageIntoView(View imageView);

    protected abstract int getLayoutId();

}
