package com.weezlabs.imagegallery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.weezlabs.imagegallery.model.Folder;

import java.io.File;


public class FolderCursorAdapter extends CursorAdapter {
    public FolderCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
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
        if (cursor != null && !cursor.isClosed()) {
            Folder folder = new Folder(cursor);
            holder.mFolder.setFolderId(folder.getId());
            holder.mFolderName.setText(new File(folder.getPath()).getName());
        }
    }

    public static class ViewHolder {
        FolderView mFolder;
        TextView mFolderName;

        public ViewHolder(View view) {
            mFolder = (FolderView) view.findViewById(R.id.folder_view);
            mFolderName = (TextView) view.findViewById(R.id.folder_name_text_view);
        }
    }
}
