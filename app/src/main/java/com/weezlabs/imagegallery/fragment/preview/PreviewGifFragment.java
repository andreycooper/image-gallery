package com.weezlabs.imagegallery.fragment.preview;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.weezlabs.imagegallery.R;


public class PreviewGifFragment extends BasePreviewFragment {

    @Override
    protected void loadImageIntoView(View imageView, ImageViewState imageViewState) {
        final ImageView gifImageView = (ImageView) imageView;

        Glide.with(getActivity())
                .load(mImage.getOriginalPath())
                .asGif()
                .error(R.mipmap.ic_error)
                .into(gifImageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_gif;
    }
}
