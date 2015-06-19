package com.weezlabs.imagegallery.tool;

/**
 * Created by cooper on 19.6.15.
 */
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
}
