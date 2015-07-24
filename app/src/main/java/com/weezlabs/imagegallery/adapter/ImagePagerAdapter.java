package com.weezlabs.imagegallery.adapter;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.weezlabs.imagegallery.fragment.preview.BasePreviewFragment;
import com.weezlabs.imagegallery.model.local.LocalImage;
import com.weezlabs.imagegallery.tool.Events;

import de.greenrobot.event.EventBus;


public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private Cursor mCursor;

    public ImagePagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        mCursor = cursor;
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public Fragment getItem(int position) {
        LocalImage localImage = null;
        if (mCursor != null && mCursor.moveToPosition(position)) {
            localImage = new LocalImage(mCursor);
        }
        return BasePreviewFragment.newInstance(localImage);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        String imageName = null;
        if (mCursor != null && mCursor.moveToPosition(position)) {
            imageName = LocalImage.getDisplayName(mCursor);
        }
        if (!TextUtils.isEmpty(imageName)) {
            EventBus.getDefault().post(new Events.ChangeTitleEvent(imageName));
        }
    }

    public void changeCursor(Cursor newCursor) {
        Cursor oldCursor = mCursor;
        if (oldCursor != null && !oldCursor.isClosed()) {
            oldCursor.close();
        }
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
