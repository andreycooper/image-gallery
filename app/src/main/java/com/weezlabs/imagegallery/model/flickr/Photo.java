package com.weezlabs.imagegallery.model.flickr;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weezlabs.imagegallery.model.Image;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.util.FileUtils;


public class Photo implements Parcelable, Image {

    public static final long FLICKR_BUCKET_ID = -993;
    public static final String TABLE = "flickr_photos";
    public static final String ID = "_id";
    public static final String FLICKR_ID = "flickr_id";
    public static final String OWNER = "owner";
    public static final String SECRET = "secret";
    public static final String SERVER = "server";
    public static final String FARM = "farm";
    public static final String TITLE = "title";
    public static final String PUBLIC = "is_public";
    public static final String FRIEND = "is_friend";
    public static final String FAMILY = "is_family";
    public static final String ROTATION = "rotation";
    public static final String ORIGINAL_SECRET = "original_secret";
    public static final String ORIGINAL_FORMAT = "original_format";
    public static final String TAKEN_DATE = "taken_date";
    public static final String LAST_UPDATE = "last_update";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    public static final String[] PROJECTION_ALL = {
            getTableColumn(ID),
            getTableColumn(FLICKR_ID),
            getTableColumn(OWNER),
            getTableColumn(SECRET),
            getTableColumn(SERVER),
            getTableColumn(FARM),
            getTableColumn(TITLE),
            getTableColumn(PUBLIC),
            getTableColumn(FRIEND),
            getTableColumn(FAMILY),
            getTableColumn(ROTATION),
            getTableColumn(ORIGINAL_SECRET),
            getTableColumn(ORIGINAL_FORMAT),
            getTableColumn(TAKEN_DATE),
            getTableColumn(LAST_UPDATE),
            getTableColumn(WIDTH),
            getTableColumn(HEIGHT)
    };

    public static String getTableColumn(String column) {
        return TABLE + "." + column;
    }

    private long mId;

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
    private long mTakenDate;
    private long mLastUpdate;

    private int mWidth;
    private int mHeight;

    public Photo() {
    }

    public Photo(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(ID));
        mFlickrId = cursor.getLong(cursor.getColumnIndex(FLICKR_ID));
        mOwner = cursor.getString(cursor.getColumnIndex(OWNER));
        mSecret = cursor.getString(cursor.getColumnIndex(SECRET));
        mServerId = cursor.getInt(cursor.getColumnIndex(SERVER));
        mFarmId = cursor.getInt(cursor.getColumnIndex(FARM));
        mTitle = cursor.getString(cursor.getColumnIndex(TITLE));
        mIsPublic = cursor.getInt(cursor.getColumnIndex(PUBLIC));
        mIsFriend = cursor.getInt(cursor.getColumnIndex(FRIEND));
        mIsFamily = cursor.getInt(cursor.getColumnIndex(FAMILY));
        mRotation = cursor.getInt(cursor.getColumnIndex(ROTATION));
        mOriginalSecret = cursor.getString(cursor.getColumnIndex(ORIGINAL_SECRET));
        mOriginalFormat = cursor.getString(cursor.getColumnIndex(ORIGINAL_FORMAT));
        mTakenDate = cursor.getLong(cursor.getColumnIndex(TAKEN_DATE));
        mLastUpdate = cursor.getLong(cursor.getColumnIndex(LAST_UPDATE));
        mWidth = cursor.getInt(cursor.getColumnIndex(WIDTH));
        mHeight = cursor.getInt(cursor.getColumnIndex(HEIGHT));
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
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

    public void setTakenDate(long takenDate) {
        mTakenDate = takenDate;
    }

    public long getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        mLastUpdate = lastUpdate;
    }

    @Override
    public String getTitle() {
        return mTitle;
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
    public long getTakenDate() {
        return mTakenDate;
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
    public long getBucketId() {
        return FLICKR_BUCKET_ID;
    }

    @Override
    public long getSize() {
        return 0;
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
        sb.append(", mTakenDate=").append(mTakenDate);
        sb.append(", mLastUpdate=").append(mLastUpdate);
        sb.append(", mWidth=").append(mWidth);
        sb.append(", mHeight=").append(mHeight);
        sb.append('}');
        return sb.toString();
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
        dest.writeLong(this.mTakenDate);
        dest.writeLong(this.mLastUpdate);
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
        this.mTakenDate = in.readLong();
        this.mLastUpdate = in.readLong();
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public static final class ContentBuilder {
        private final ContentValues mValues = new ContentValues();

        public ContentBuilder id(long id) {
            mValues.put(ID, id);
            return this;
        }

        public ContentBuilder flickrId(long flickrId) {
            mValues.put(FLICKR_ID, flickrId);
            return this;
        }

        public ContentBuilder owner(String owner) {
            mValues.put(OWNER, owner);
            return this;
        }

        public ContentBuilder secret(String secret) {
            mValues.put(SECRET, secret);
            return this;
        }

        public ContentBuilder server(int serverId) {
            mValues.put(SERVER, serverId);
            return this;
        }

        public ContentBuilder farm(int farmId) {
            mValues.put(FARM, farmId);
            return this;
        }

        public ContentBuilder title(String title) {
            mValues.put(TITLE, title);
            return this;
        }

        public ContentBuilder isPublic(int isPublic) {
            mValues.put(PUBLIC, isPublic);
            return this;
        }

        public ContentBuilder isFriend(int isFriend) {
            mValues.put(FRIEND, isFriend);
            return this;
        }

        public ContentBuilder isFamily(int isFamily) {
            mValues.put(FAMILY, isFamily);
            return this;
        }

        public ContentBuilder rotation(int rotation) {
            mValues.put(ROTATION, rotation);
            return this;
        }

        public ContentBuilder originalSecret(String originalSecret) {
            mValues.put(ORIGINAL_SECRET, originalSecret);
            return this;
        }

        public ContentBuilder originalFormat(String originalFormat) {
            mValues.put(ORIGINAL_FORMAT, originalFormat);
            return this;
        }

        public ContentBuilder takenDate(long takenDate) {
            mValues.put(TAKEN_DATE, takenDate);
            return this;
        }

        public ContentBuilder lastUpdate(long lastUpdate) {
            mValues.put(LAST_UPDATE, lastUpdate);
            return this;
        }

        public ContentBuilder width(int width) {
            mValues.put(WIDTH, width);
            return this;
        }

        public ContentBuilder height(int height) {
            mValues.put(HEIGHT, height);
            return this;
        }

        public ContentBuilder clear(){
            mValues.clear();
            return this;
        }

        public ContentValues build() {
            return mValues;
        }
    }

}
