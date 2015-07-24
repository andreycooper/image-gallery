package com.weezlabs.imagegallery.model.flickr;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.util.FileUtils;


public class Photo implements Parcelable, Image {

    @Expose
    @SerializedName("id")
    private long mFlickrId;
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

    private int mRotation;
    private String mOriginalSecret;
    private String mOriginalFormat;
    private String mTakenDate;

    private int mWidth;
    private int mHeight;

    public Photo() {
    }

    public long getFlickrId() {
        return mFlickrId;
    }

    public void setFlickrId(long flickrId) {
        mFlickrId = flickrId;
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

    public int getRotation() {
        return mRotation;
    }

    public void setRotation(int rotation) {
        mRotation = rotation;
    }

    public String getOriginalSecret() {
        return mOriginalSecret;
    }

    public void setOriginalSecret(String originalSecret) {
        mOriginalSecret = originalSecret;
    }

    public String getOriginalFormat() {
        return mOriginalFormat;
    }

    public void setOriginalFormat(String originalFormat) {
        mOriginalFormat = originalFormat;
    }

    public String getTakenDate() {
        return mTakenDate;
    }

    public void setTakenDate(String takenDate) {
        mTakenDate = takenDate;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public String getPath() {
        return FlickrStorage.getPhotoUrl(this);
    }

    @Override
    public String getOriginalPath() {
        return FlickrStorage.getOriginalPhotoUrl(this);
    }

    @Override
    public String getMimeType() {
        return FileUtils.IMAGE_TYPE + getOriginalFormat();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Photo{");
        sb.append("mFlickrId=").append(mFlickrId);
        sb.append(", mOwner='").append(mOwner).append('\'');
        sb.append(", mSecret='").append(mSecret).append('\'');
        sb.append(", mServerId=").append(mServerId);
        sb.append(", mFarmId=").append(mFarmId);
        sb.append(", mTitle='").append(mTitle).append('\'');
        sb.append(", mIsPublic=").append(mIsPublic);
        sb.append(", mIsFriend=").append(mIsFriend);
        sb.append(", mIsFamily=").append(mIsFamily);
        sb.append(", mRotation=").append(mRotation);
        sb.append(", mOriginalSecret='").append(mOriginalSecret).append('\'');
        sb.append(", mOriginalFormat='").append(mOriginalFormat).append('\'');
        sb.append(", mTakenDate='").append(mTakenDate).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (mFlickrId != photo.mFlickrId) return false;
        if (mServerId != photo.mServerId) return false;
        return mFarmId == photo.mFarmId;

    }

    @Override
    public int hashCode() {
        int result = (int) (mFlickrId ^ (mFlickrId >>> 32));
        result = 31 * result + mServerId;
        result = 31 * result + mFarmId;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mFlickrId);
        dest.writeString(this.mOwner);
        dest.writeString(this.mSecret);
        dest.writeInt(this.mServerId);
        dest.writeInt(this.mFarmId);
        dest.writeString(this.mTitle);
        dest.writeInt(this.mIsPublic);
        dest.writeInt(this.mIsFriend);
        dest.writeInt(this.mIsFamily);
        dest.writeInt(this.mRotation);
        dest.writeString(this.mOriginalSecret);
        dest.writeString(this.mOriginalFormat);
        dest.writeString(this.mTakenDate);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
    }

    protected Photo(Parcel in) {
        this.mFlickrId = in.readLong();
        this.mOwner = in.readString();
        this.mSecret = in.readString();
        this.mServerId = in.readInt();
        this.mFarmId = in.readInt();
        this.mTitle = in.readString();
        this.mIsPublic = in.readInt();
        this.mIsFriend = in.readInt();
        this.mIsFamily = in.readInt();
        this.mRotation = in.readInt();
        this.mOriginalSecret = in.readString();
        this.mOriginalFormat = in.readString();
        this.mTakenDate = in.readString();
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

}
