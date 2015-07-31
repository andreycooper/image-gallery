package com.weezlabs.imagegallery.view.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.view.widget.FolderView;


public class FolderViewHolder extends ViewHolder {
    public FolderView folderView;
    public TextView folderName;

    public FolderViewHolder(View view) {
        super(view);
        folderView = findWidgetById(R.id.folder_view);
        folderName = findWidgetById(R.id.folder_name_text_view);
    }
}
