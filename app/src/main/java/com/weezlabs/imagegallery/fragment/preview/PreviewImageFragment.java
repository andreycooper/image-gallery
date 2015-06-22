package com.weezlabs.imagegallery.fragment.preview;


import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.weezlabs.imagegallery.R;


public class PreviewImageFragment extends BasePreviewFragment {

    public PreviewImageFragment() {
        // Required empty public constructor
    }

    @Override
    protected void loadImageIntoView(View imageView) {
        final SubsamplingScaleImageView scaleImageView = (SubsamplingScaleImageView) imageView;

        Glide.with(getActivity())
                .load(mImagePath)
                .asBitmap()
                .into(new ViewTarget<SubsamplingScaleImageView, Bitmap>(scaleImageView) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        SubsamplingScaleImageView scaleImageView = this.view;
                        scaleImageView.setImage(ImageSource.bitmap(resource));
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_image;
    }


}
