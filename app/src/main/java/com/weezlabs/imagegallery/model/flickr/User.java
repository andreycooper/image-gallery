package com.weezlabs.imagegallery.model.flickr;


import com.weezlabs.imagegallery.storage.FlickrStorage;

public class User {
    private String mFlickrId;
    private String mUsername;
    private String mRealName;
    private String mNsId;
    private int mIconFarmId;
    private int mIconServerId;

    public User(String flickrId, String username) {
        mFlickrId = flickrId;
        mUsername = username;
    }

    public User(String flickrId, String username, String realName, String nsId, int iconFarmId,
                int iconServerId) {
        this(flickrId, username);
        mRealName = realName;
        mNsId = nsId;
        mIconFarmId = iconFarmId;
        mIconServerId = iconServerId;
    }

    public String getFlickrId() {
        return mFlickrId;
    }

    public void setFlickrId(String flickrId) {
        mFlickrId = flickrId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getRealName() {
        return mRealName;
    }

    public void setRealName(String realName) {
        mRealName = realName;
    }

    public String getNsId() {
        return mNsId;
    }

    public void setNsId(String nsId) {
        mNsId = nsId;
    }

    public int getIconFarmId() {
        return mIconFarmId;
    }

    public void setIconFarmId(int iconFarmId) {
        mIconFarmId = iconFarmId;
    }

    public int getIconServerId() {
        return mIconServerId;
    }

    public void setIconServerId(int iconServerId) {
        mIconServerId = iconServerId;
    }

    public String getIconUrl() {
        return FlickrStorage.getIconUrl(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (mIconFarmId != user.mIconFarmId) return false;
        if (mIconServerId != user.mIconServerId) return false;
        if (!mFlickrId.equals(user.mFlickrId)) return false;
        if (!mUsername.equals(user.mUsername)) return false;
        if (mRealName != null ? !mRealName.equals(user.mRealName) : user.mRealName != null)
            return false;
        return !(mNsId != null ? !mNsId.equals(user.mNsId) : user.mNsId != null);

    }

    @Override
    public int hashCode() {
        int result = mFlickrId.hashCode();
        result = 31 * result + mUsername.hashCode();
        result = 31 * result + (mRealName != null ? mRealName.hashCode() : 0);
        result = 31 * result + (mNsId != null ? mNsId.hashCode() : 0);
        result = 31 * result + mIconFarmId;
        result = 31 * result + mIconServerId;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("mFlickrId='").append(mFlickrId).append('\'');
        sb.append(", mUsername='").append(mUsername).append('\'');
        sb.append(", mRealName='").append(mRealName).append('\'');
        sb.append(", mNsId='").append(mNsId).append('\'');
        sb.append(", mIconFarmId=").append(mIconFarmId);
        sb.append(", mIconServerId=").append(mIconServerId);
        sb.append('}');
        return sb.toString();
    }
}
