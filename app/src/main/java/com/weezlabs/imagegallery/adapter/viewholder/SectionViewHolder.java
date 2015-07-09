package com.weezlabs.imagegallery.adapter.viewholder;


import android.view.View;
import android.widget.TextView;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

public class SectionViewHolder extends ViewHolder {
    public TextView sectionText;

    public SectionViewHolder(View rootView) {
        super(rootView);
        sectionText = (TextView) rootView;
    }
}
