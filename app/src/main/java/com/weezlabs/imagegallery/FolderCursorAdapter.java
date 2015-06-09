package com.weezlabs.imagegallery;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.db.ImageContentProvider;
import com.weezlabs.imagegallery.model.Folder;
import com.weezlabs.imagegallery.model.FolderViewModel;
import com.weezlabs.imagegallery.model.ImageFile;
import com.weezlabs.imagegallery.widget.FolderView;

import static com.weezlabs.imagegallery.model.FolderViewModel.MAX_COUNT_IMAGES;


public class FolderCursorAdapter extends CursorAdapter {
    private LongSparseArray<FolderViewModel> mSparseArray;

    public FolderCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mSparseArray = new LongSparseArray<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        ViewHolder holder = new ViewHolder(rowView);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        Folder folder = new Folder(cursor);
        String folderName = folder.getPath().substring(folder.getPath().lastIndexOf('/') + 1);

        FolderViewModel folderViewModel = getFolderViewModel(context, cursor, folder.getId());

        fillHolderViews(context, holder, folderName, folderViewModel);
    }

    private FolderViewModel getFolderViewModel(Context context, Cursor cursor, long folderId) {
        FolderViewModel folderViewModel = mSparseArray.get(folderId, null);
        if (folderViewModel == null) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor imagesCursor = contentResolver.query(ImageContentProvider.buildFolderImagesUri(folderId),
                    ImageFile.PROJECTION_ALL, null, null, null);
            folderViewModel = new FolderViewModel(imagesCursor);
            if (imagesCursor != null && !cursor.isClosed()) {
                imagesCursor.close();
            }
            mSparseArray.append(folderId, folderViewModel);
        }
        return folderViewModel;
    }

    private void fillHolderViews(Context context, ViewHolder holder, String folderName, FolderViewModel folderViewModel) {
        holder.mFolderName.setText(folderName);
        for (int i = 0; i < folderViewModel.getImagePaths().size(); i++) {
            holder.mFolderView.getImageViews()[i].setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(folderViewModel.getImagePaths().get(i))
                    .centerCrop()
                    .into(holder.mFolderView.getImageViews()[i]);
        }
        for (int i = folderViewModel.getImagePaths().size(); i < MAX_COUNT_IMAGES; i++) {
            holder.mFolderView.getImageViews()[i].setVisibility(View.GONE);
            holder.mFolderView.getImageViews()[i].setImageBitmap(null);
        }

        holder.mFolderView.setCountText(context.getString(R.string.label_folder_view_images_count,
                folderViewModel.getImageCount()));
    }

    public static class ViewHolder {
        FolderView mFolderView;
        TextView mFolderName;

        public ViewHolder(View view) {
            mFolderView = (FolderView) view.findViewById(R.id.folder_view);
            mFolderName = (TextView) view.findViewById(R.id.folder_name_text_view);
        }
    }
}
