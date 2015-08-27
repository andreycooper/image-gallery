package com.weezlabs.imagegallery.fragment.preview;


import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.local.LocalImage;
import com.weezlabs.imagegallery.tool.Events.LoadThumbnailEvent;
import com.weezlabs.imagegallery.tool.SkiaImageDecoder;
import com.weezlabs.imagegallery.tool.SkiaImageRegionDecoder;
import com.weezlabs.imagegallery.util.TextUtils;

import java.io.File;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

import static android.provider.MediaStore.Images.Thumbnails.DATA;
import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static android.provider.MediaStore.Images.Thumbnails.getThumbnail;
import static android.provider.MediaStore.Images.Thumbnails.queryMiniThumbnail;


public class PreviewImageFragment extends BasePreviewFragment {

    public PreviewImageFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_image;
    }

    @Override
    protected void loadImageIntoView(View imageView) {
        final SubsamplingScaleImageView scaleImageView = (SubsamplingScaleImageView) imageView;
        // It's needed to prevent a OOM exception
        Resources resources = getActivity().getApplicationContext().getResources();
        scaleImageView.setMinimumTileDpi(resources.getInteger(R.integer.preview_image_tile_dpi));

        scaleImageView.setOnImageEventListener(new OnImageLoadingListener(mProgressBar));
        scaleImageView.setBitmapDecoderClass(SkiaImageDecoder.class);
        scaleImageView.setRegionDecoderClass(SkiaImageRegionDecoder.class);

        if (mImage instanceof LocalImage) {
            Uri uri = Uri.fromFile(new File(mImage.getOriginalPath()));
            String thumbUriString = getThumbUriString();
            if (TextUtils.isNonEmpty(thumbUriString)) {
                scaleImageView.setImage(ImageSource.uri(uri).tilingEnabled()
                                .dimensions(mImage.getWidth(), mImage.getHeight()),
                        ImageSource.uri(thumbUriString));
            } else {
                // needed to prevent setup a ViewPager into wrong position, because getThumbnail()
                // notify listeners to reload a cursor
                EventBus.getDefault().post(new LoadThumbnailEvent());
                scaleImageView.setImage(ImageSource.uri(uri).tilingEnabled()
                                .dimensions(mImage.getWidth(), mImage.getHeight()),
                        ImageSource.bitmap(getThumbnailBitmap()));
            }
        } else if (mImage instanceof Photo) {
            PhotoTarget photoTarget = new PhotoTarget(scaleImageView, mImage);
            Glide.with(getActivity())
                    .load(mImage.getOriginalPath())
                    .downloadOnly(photoTarget);
        }
    }

    private Bitmap getThumbnailBitmap() {
        return getThumbnail(getActivity().getContentResolver(),
                ((LocalImage) mImage).getId(), MINI_KIND, null);
    }

    @Nullable
    private String getThumbUriString() {
        String thumbUriString = null;
        Cursor thumbCursor = queryMiniThumbnail(getActivity().getContentResolver(),
                ((LocalImage) mImage).getId(), MINI_KIND, null);
        if (thumbCursor != null && thumbCursor.moveToFirst()) {
            thumbUriString = thumbCursor.getString((thumbCursor.getColumnIndex(DATA)));
            thumbCursor.close();
        }
        return thumbUriString;
    }


    public static class PhotoTarget extends SimpleTarget<File> {
        private final SubsamplingScaleImageView mScaleImageView;
        private final int mWidth;
        private final int mHeight;

        public PhotoTarget(SubsamplingScaleImageView scaleImageView, Image photo) {
            super(photo.getWidth(), photo.getHeight());
            mScaleImageView = scaleImageView;
            mWidth = photo.getWidth();
            mHeight = photo.getHeight();
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            Timber.e("Error while Photo loading: %s", e.getMessage());
            e.printStackTrace();
        }

        @Override
        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
            Uri fileUri = Uri.fromFile(resource);
            mScaleImageView.setImage(ImageSource.uri(fileUri)
                    .tilingEnabled()
                    .dimensions(mWidth, mHeight));
        }

    }

    public static class OnImageLoadingListener implements SubsamplingScaleImageView.OnImageEventListener {
        private final ProgressBar mLoadingProgressBar;

        public OnImageLoadingListener(ProgressBar progressBar) {
            mLoadingProgressBar = progressBar;
        }

        @Override
        public void onReady() {
            mLoadingProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onImageLoaded() {
            mLoadingProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPreviewLoadError(Exception e) {
            mLoadingProgressBar.setVisibility(View.GONE);
            Timber.e("Error while a SubScaleImage preview loading: %s", e.getMessage());
            e.printStackTrace();
        }

        @Override
        public void onImageLoadError(Exception e) {
            mLoadingProgressBar.setVisibility(View.GONE);
            Timber.e("Error while a SubScaleImage loading: %s", e.getMessage());
            e.printStackTrace();
        }

        @Override
        public void onTileLoadError(Exception e) {
            Timber.e("Error while a SubScaleImage tile loading: %s", e.getMessage());
            e.printStackTrace();
        }
    }
}
