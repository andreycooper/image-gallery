package com.weezlabs.imagegallery.fragment;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;


public class PreviewGifFragment extends BasePreviewFragment {

    @Override
    protected void loadImageIntoView(View imageView) {
        ImageView gifImageView = (ImageView) imageView;

        Glide.with(getActivity())
                .load(mImagePath)
                .into(gifImageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_gif;
    }
}
