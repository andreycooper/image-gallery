package com.weezlabs.imagegallery.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;
import com.weezlabs.imagegallery.R;


public class ImageViewHolder extends ViewHolder {
    public ImageView image;
    public View infoLayout;
    public TextView imageName;
    public TextView imageDate;
    public TextView imageSize;

    public ImageViewHolder(View view) {
        super(view);
        image = findWidgetById(R.id.image_view);
        infoLayout = findWidgetById(R.id.info_layout);
        imageName = findWidgetById(R.id.image_name_text_view);
        imageDate = findWidgetById(R.id.image_date_text_view);
        imageSize = findWidgetById(R.id.image_size_text_view);
    }

}
