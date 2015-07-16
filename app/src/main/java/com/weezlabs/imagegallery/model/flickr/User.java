package com.weezlabs.imagegallery.model.flickr;


public class User {
    private String mFlickrId;
    private String mUsername;

    public User(String flickrId, String username) {
        mFlickrId = flickrId;
        mUsername = username;
    }

    public String getFlickrId() {
        return mFlickrId;
    }

    public String getUsername() {
        return mUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return mFlickrId.equals(user.mFlickrId) && mUsername.equals(user.mUsername);

    }

    @Override
    public int hashCode() {
        int result = mFlickrId.hashCode();
        result = 31 * result + mUsername.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("mFlickrId='").append(mFlickrId).append('\'');
        sb.append(", mUsername='").append(mUsername).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
