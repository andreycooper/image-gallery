package com.weezlabs.imagegallery.model.flickr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Photo {

    @Expose
    @SerializedName("id")
    private long mExternalId;
    @Expose
    @SerializedName("owner")
    private String mOwner;
    @Expose
    @SerializedName("secret")
    private String mSecret;
    @Expose
    @SerializedName("server")
    private int mServerId;
    @Expose
    @SerializedName("farm")
    private int mFarmId;
    @Expose
    @SerializedName("title")
    private String mTitle;
    @Expose
    @SerializedName("ispublic")
    private int mIsPublic;
    @Expose
    @SerializedName("isfriend")
    private int mIsFriend;
    @Expose
    @SerializedName("isfamily")
    private int mIsFamily;

    public Photo() {
    }

    public Photo(long externalId, String owner, String secret, int serverId, int farmId,
                 String title, int isPublic, int isFriend, int isFamily) {
        mExternalId = externalId;
        mOwner = owner;
        mSecret = secret;
        mServerId = serverId;
        mFarmId = farmId;
        mTitle = title;
        mIsPublic = isPublic;
        mIsFriend = isFriend;
        mIsFamily = isFamily;
    }

    public long getExternalId() {
        return mExternalId;
    }

    public void setExternalId(long externalId) {
        mExternalId = externalId;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public int getServerId() {
        return mServerId;
    }

    public void setServerId(int serverId) {
        mServerId = serverId;
    }

    public int getFarmId() {
        return mFarmId;
    }

    public void setFarmId(int farmId) {
        mFarmId = farmId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getIsPublic() {
        return mIsPublic;
    }

    public void setIsPublic(int isPublic) {
        mIsPublic = isPublic;
    }

    public int getIsFriend() {
        return mIsFriend;
    }

    public void setIsFriend(int isFriend) {
        mIsFriend = isFriend;
    }

    public int getIsFamily() {
        return mIsFamily;
    }

    public void setIsFamily(int isFamily) {
        mIsFamily = isFamily;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Photo{");
        sb.append("mExternalId=").append(mExternalId);
        sb.append(", mOwner='").append(mOwner).append('\'');
        sb.append(", mSecret='").append(mSecret).append('\'');
        sb.append(", mServerId=").append(mServerId);
        sb.append(", mFarmId=").append(mFarmId);
        sb.append(", mTitle='").append(mTitle).append('\'');
        sb.append(", mIsPublic=").append(mIsPublic);
        sb.append(", mIsFriend=").append(mIsFriend);
        sb.append(", mIsFamily=").append(mIsFamily);
        sb.append('}');
        return sb.toString();
    }
}
