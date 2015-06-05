package com.weezlabs.imagegallery.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import static com.weezlabs.imagegallery.db.ImageContentProvider.FOLDERS_CONTENT_URI;
import static com.weezlabs.imagegallery.db.ImageContentProvider.INCORRECT_ID;
import static com.weezlabs.imagegallery.db.ImageContentProvider.buildFolderImagesUri;


public abstract class BaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int FOLDERS_LOADER = 113;
    public static final int IMAGES_LOADER = 335;

    public static final String FOLDER_ID = "com.weezlabs.imagegallery.extra.FOLDER_ID";

    protected void loadFoldersCursor() {
        Loader<Cursor> folderLoader = getLoaderManager().getLoader(FOLDERS_LOADER);
        if (folderLoader == null) {
            folderLoader = getLoaderManager().initLoader(FOLDERS_LOADER, null, this);
        } else {
            folderLoader = getLoaderManager().restartLoader(FOLDERS_LOADER, null, this);
        }
        folderLoader.forceLoad();
    }

    protected void loadImagesCursor(long folderId) {
        Bundle args = new Bundle();
        args.putLong(FOLDER_ID, folderId);
        Loader<Cursor> imagesLoader = getLoaderManager().getLoader(IMAGES_LOADER);
        if (imagesLoader == null) {
            imagesLoader = getLoaderManager().initLoader(IMAGES_LOADER, args, this);
        } else {
            imagesLoader = getLoaderManager().restartLoader(IMAGES_LOADER, args, this);
        }
        imagesLoader.forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long folderId = INCORRECT_ID;
        if (args != null) {
            folderId = args.getLong(FOLDER_ID, INCORRECT_ID);
        }
        switch (id) {
            case FOLDERS_LOADER:
                return new CursorLoader(getActivity(), FOLDERS_CONTENT_URI, null, null, null, null);
            case IMAGES_LOADER:
                return folderId != INCORRECT_ID ?
                        new CursorLoader(getActivity(), buildFolderImagesUri(folderId), null, null, null, null) : null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO: override this method
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO: override this method
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
