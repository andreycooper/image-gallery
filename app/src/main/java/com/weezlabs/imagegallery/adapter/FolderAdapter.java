package com.weezlabs.imagegallery.adapter;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.Bucket;
import com.weezlabs.imagegallery.model.FolderViewModel;
import com.weezlabs.imagegallery.widget.FolderView;

import static com.weezlabs.imagegallery.model.FolderViewModel.MAX_COUNT_IMAGES;


public class FolderAdapter extends CursorAdapter {

    private LongSparseArray<FolderViewModel> mSparseArray;
    private int mLayoutResource;

    public FolderAdapter(Context context, Cursor c, int layout) {
        super(context, c, true);
        mSparseArray = new LongSparseArray<>();
        mLayoutResource = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(mLayoutResource, parent, false);
        FolderViewHolder holder = new FolderViewHolder(rowView);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FolderViewHolder holder = (FolderViewHolder) view.getTag();

        Bucket bucket = new Bucket(cursor);
        FolderViewModel folderViewModel = getFolderViewModel(context, bucket.getBucketId());
        fillHolderViews(context, holder, bucket.getBucketName(), folderViewModel);

    }

    public Bucket getBucket(int clickedPosition) {
        return new Bucket((Cursor) getItem(clickedPosition));
    }

    private void fillHolderViews(Context context, FolderViewHolder holder, String folderName,
                                 FolderViewModel folderViewModel) {
        holder.mFolderName.setText(folderName);
        for (int i = 0; i < folderViewModel.getImages().size(); i++) {
            holder.mFolderView.getImageViews()[i].setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(folderViewModel.getImages().get(i).getPath())
                    .placeholder(R.drawable.ic_image_placeholder_48dp)
                    .centerCrop()
                    .crossFade()
                    .into(holder.mFolderView.getImageViews()[i]);
        }
        for (int i = folderViewModel.getImages().size(); i < MAX_COUNT_IMAGES; i++) {
            holder.mFolderView.getImageViews()[i].setVisibility(View.GONE);
            holder.mFolderView.getImageViews()[i].setImageBitmap(null);
        }

        holder.mFolderView.setCountText(context.getString(R.string.label_folder_view_images_count,
                folderViewModel.getImageCount()));
    }

    private FolderViewModel getFolderViewModel(Context context, long bucketId) {
        FolderViewModel folderViewModel = mSparseArray.get(bucketId, null);
        if (folderViewModel == null) {
            CursorLoader loader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media.BUCKET_ID + "=?",
                    new String[]{String.valueOf(bucketId)}, null);
            Cursor imagesCursor = loader.loadInBackground();
            folderViewModel = new FolderViewModel(imagesCursor);
            if (imagesCursor != null && !imagesCursor.isClosed()) {
                imagesCursor.close();
            }
            mSparseArray.append(bucketId, folderViewModel);
        }
        return folderViewModel;
    }

    public static class FolderViewHolder {
        FolderView mFolderView;
        TextView mFolderName;

        public FolderViewHolder(View view) {
            mFolderView = (FolderView) view.findViewById(R.id.folder_view);
            mFolderName = (TextView) view.findViewById(R.id.folder_name_text_view);
        }
    }
}
