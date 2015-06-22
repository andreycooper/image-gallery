package com.weezlabs.imagegallery.fragment.preview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.util.Utils;

import de.greenrobot.event.EventBus;


public abstract class BasePreviewFragment extends Fragment {
    protected static final String IMAGE_PATH = "com.weezlabs.imagegallery.IMAGE_PATH";

    protected String mImagePath;

    public static BasePreviewFragment newInstance(String imagePath) {
        BasePreviewFragment fragment;
        if (Utils.isGifFile(imagePath)) {
            fragment = new PreviewGifFragment();
        } else {
            fragment = new PreviewImageFragment();
        }
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImagePath = getArguments().getString(IMAGE_PATH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        View imageView = rootView.findViewById(R.id.image_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Events.ToolbarVisibilityEvent());
            }
        });

        loadImageIntoView(imageView);

        return rootView;
    }

    protected abstract void loadImageIntoView(View imageView);

    protected abstract int getLayoutId();

}
