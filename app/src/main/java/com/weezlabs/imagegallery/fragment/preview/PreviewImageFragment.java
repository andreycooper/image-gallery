package com.weezlabs.imagegallery.fragment.preview;


import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.model.flickr.Photo;
import com.weezlabs.imagegallery.model.local.LocalImage;
import com.weezlabs.imagegallery.tool.Events.LoadThumbnailEvent;
import com.weezlabs.imagegallery.tool.SkiaImageDecoder;
import com.weezlabs.imagegallery.tool.SkiaImageRegionDecoder;
import com.weezlabs.imagegallery.util.FileUtils;
import com.weezlabs.imagegallery.util.TextUtils;

import java.io.File;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
    public void onSaveInstanceState(Bundle outState) {
        View rootView = getView();
        if (rootView != null) {
            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) rootView
                    .findViewById(R.id.image_view);
            ImageViewState state = imageView.getState();
            if (state != null) {
                outState.putSerializable(BUNDLE_STATE, imageView.getState());
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_preview_image;
    }

    @Override
    protected void loadImageIntoView(View imageView, ImageViewState imageViewState) {
        final SubsamplingScaleImageView scaleImageView = (SubsamplingScaleImageView) imageView;
        // It's needed to prevent a OOM exception
        Resources resources = getActivity().getApplicationContext().getResources();
        scaleImageView.setMinimumTileDpi(resources.getInteger(R.integer.preview_image_tile_dpi));

        scaleImageView.setOnImageEventListener(new OnImageLoadingListener(mProgressBar));
        scaleImageView.setBitmapDecoderClass(SkiaImageDecoder.class);
        scaleImageView.setRegionDecoderClass(SkiaImageRegionDecoder.class);

        if (mImage instanceof LocalImage) {
            showLocalImage(imageViewState, scaleImageView);
        } else if (mImage instanceof Photo) {
            PhotoTarget photoTarget = new PhotoTarget(scaleImageView, mImage, imageViewState);
            Glide.with(getActivity())
                    .load(mImage.getOriginalPath())
                    .downloadOnly(photoTarget);
        }
    }

    private void showLocalImage(ImageViewState imageViewState, SubsamplingScaleImageView scaleImageView) {
        Uri imageUri = Uri.fromFile(new File(mImage.getOriginalPath()));

        String thumbUriString = getThumbUriString(((LocalImage) mImage).getId());
        if (TextUtils.isNonEmpty(thumbUriString) && FileUtils.isFileExist(thumbUriString)) {
            scaleImageView.setImage(
                    ImageSource.uri(imageUri)
                            .tilingEnabled()
                            .dimensions(mImage.getWidth(), mImage.getHeight()),
                    ImageSource.uri(thumbUriString),
                    imageViewState);
        } else {
            Observable.just(mImage)
                    .map(image -> (LocalImage) image)
                    .map(localImage -> getThumbnailBitmap(localImage.getId()))
                    .flatMap(bitmap -> {
                        if (bitmap != null) {
                            return Observable.just(bitmap);
                        } else {
                            return Observable.error(new NullPointerException("Thumbnail's bitmap is null!"));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bitmap -> {
                        scaleImageView.setImage(
                                ImageSource.uri(imageUri)
                                        .tilingEnabled()
                                        .dimensions(mImage.getWidth(), mImage.getHeight()),
                                ImageSource.bitmap(bitmap),
                                imageViewState);
                    }, throwable -> {
                        mProgressBar.setVisibility(View.GONE);
                        scaleImageView.setImage(ImageSource.resource(R.mipmap.ic_error));
                        // TODO: show a toast only for current fragment!
                        Timber.e("Error while getting thumbnail: %s", throwable.getMessage());
                        throwable.printStackTrace();
                    });
        }
    }

    private Bitmap getThumbnailBitmap(int id) {
        // needed to prevent setup a ViewPager into wrong position, because getThumbnail()
        // notify listeners to reload a cursor
        EventBus.getDefault().post(new LoadThumbnailEvent());

        return getThumbnail(getActivity().getContentResolver(), id, MINI_KIND, null);
    }

    @Nullable
    private String getThumbUriString(int id) {
        String thumbUriString = null;
        Cursor thumbCursor = queryMiniThumbnail(getActivity().getContentResolver(),
                id, MINI_KIND, null);
        if (thumbCursor != null && thumbCursor.moveToFirst()) {
            thumbUriString = thumbCursor.getString((thumbCursor.getColumnIndex(DATA)));
        }
        if (thumbCursor != null && !thumbCursor.isClosed()) {
            thumbCursor.close();
        }
        return thumbUriString;
    }


    private static class PhotoTarget extends SimpleTarget<File> {
        private final SubsamplingScaleImageView mScaleImageView;
        private final ImageViewState mImageViewState;
        private final int mWidth;
        private final int mHeight;

        public PhotoTarget(SubsamplingScaleImageView scaleImageView, Image photo, ImageViewState imageViewState) {
            super(photo.getWidth(), photo.getHeight());
            mScaleImageView = scaleImageView;
            mWidth = photo.getWidth();
            mHeight = photo.getHeight();
            mImageViewState = imageViewState;
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
            mScaleImageView.setImage(
                    ImageSource.uri(fileUri)
                            .tilingEnabled()
                            .dimensions(mWidth, mHeight),
                    mImageViewState);
        }

    }

    private static class OnImageLoadingListener implements SubsamplingScaleImageView.OnImageEventListener {
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
            mLoadingProgressBar.setVisibility(View.GONE);
            Timber.e("Error while a SubScaleImage tile loading: %s", e.getMessage());
            e.printStackTrace();
        }
    }
}
