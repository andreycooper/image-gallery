package com.weezlabs.imagegallery.fragment.preview;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;


public class PreviewGifFragment extends BasePreviewFragment {

    @Override
    protected void loadImageIntoView(View imageView) {
        final ImageView gifImageView = (ImageView) imageView;

        Glide.with(getActivity())
                .load(mImage.getPath())
                .into(gifImageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_gif;
    }
}
