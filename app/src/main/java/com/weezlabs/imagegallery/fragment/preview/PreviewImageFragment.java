package com.weezlabs.imagegallery.fragment.preview;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.local.LocalImage;

import java.io.File;


public class PreviewImageFragment extends BasePreviewFragment {

    public PreviewImageFragment() {
        // Required empty public constructor
    }

    @Override
    protected void loadImageIntoView(View imageView) {
        final SubsamplingScaleImageView scaleImageView = (SubsamplingScaleImageView) imageView;

//        if (mImage instanceof LocalImage) {
//            Uri uri = Uri.fromFile(new File(mImage.getOriginalPath()));
//            scaleImageView.setImage(ImageSource.uri(uri).tilingEnabled());
//        } else if (mImage instanceof Photo) {
//            Glide.with(getActivity())
//                    .load(mImage.getOriginalPath())
//                    .downloadOnly(new FileTarget(mImage, scaleImageView));
//        }

        Glide.with(getActivity())
                .load(mImage.getOriginalPath())
                .asBitmap()
                .into(new ViewTarget<SubsamplingScaleImageView, Bitmap>(scaleImageView) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        SubsamplingScaleImageView scaleImageView = this.view;
                        scaleImageView.setImage(ImageSource.bitmap(resource).tilingEnabled());
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_image;
    }


    private static class FileTarget implements Target<File> {
        private final SubsamplingScaleImageView mScaleImageView;
        private Request mRequest;
        private Image mImage;

        public FileTarget(Image image, SubsamplingScaleImageView scaleImageView) {
            mImage = image;
            mScaleImageView = scaleImageView;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            // TODO: maybe add placeholder later
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            // TODO: maybe add error placeholder later
        }

        @Override
        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
            Uri fileUri = Uri.fromFile(resource);
            mScaleImageView.setImage(ImageSource.uri(fileUri).tilingEnabled());
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {

        }

        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(mImage.getWidth(), mImage.getHeight());
        }

        @Override
        public void setRequest(Request request) {
            mRequest = request;
        }

        @Override
        public Request getRequest() {
            return mRequest;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }
    }
}
