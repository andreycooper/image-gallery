package com.weezlabs.imagegallery.tool;


public class Events {
    public static class ChangeTitleEvent {
        private final String mTitle;

        public ChangeTitleEvent(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    public static class ToolbarVisibilityEvent {

    }
}
