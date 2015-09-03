package com.weezlabs.imagegallery.tool;


public final class Events {
    private Events() {
    }

    public static class ChangeTitleEvent {
        private final String mTitle;

        public ChangeTitleEvent(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    public static final class ToolbarVisibilityEvent {

    }

    public static final class UserLogonEvent {

    }

    public static final class LoadFlickrPhotosEvent {

    }

    public static final class FetchCompletedEvent {
        private boolean mIsSuccess;
        public FetchCompletedEvent(Boolean isSuccess){
            mIsSuccess = isSuccess;
        }

        public boolean isSuccess() {
            return mIsSuccess;
        }
    }

    public static final class LoadThumbnailEvent {

    }
}
