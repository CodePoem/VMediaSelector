package com.vdreamers.vmediaselector.core.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册实体类
 * <p>
 * date 2019-09-18 20:24:07
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("WeakerAccess")
public class AlbumEntity implements Parcelable {
    public static final String DEFAULT_NAME = "";

    /**
     * 相册数量
     */
    public int mCount;
    /**
     * 相册是否被选择
     */
    public boolean mIsSelected;
    /**
     * 文件夹Id
     */
    public String mBucketId = DEFAULT_NAME;
    /**
     * 文件夹名
     */
    public String mBucketName;
    /**
     * 相册列表
     */
    public List<MediaEntity> mImageList = new ArrayList<>();

    public AlbumEntity() {
    }

    public static AlbumEntity createDefaultAlbum() {
        AlbumEntity result = new AlbumEntity();
        result.mIsSelected = true;
        return result;
    }

    public boolean hasImages() {
        return mImageList != null && mImageList.size() > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBucketId);
        dest.writeInt(this.mCount);
        dest.writeString(this.mBucketName);
        dest.writeList(this.mImageList);
        dest.writeByte(this.mIsSelected ? (byte) 1 : (byte) 0);
    }

    protected AlbumEntity(Parcel in) {
        this.mBucketId = in.readString();
        this.mCount = in.readInt();
        this.mBucketName = in.readString();
        this.mImageList = new ArrayList<>();
        in.readList(this.mImageList, MediaEntity.class.getClassLoader());
        this.mIsSelected = in.readByte() != 0;
    }

    public static final Creator<AlbumEntity> CREATOR = new Creator<AlbumEntity>() {
        @Override
        public AlbumEntity createFromParcel(Parcel source) {
            return new AlbumEntity(source);
        }

        @Override
        public AlbumEntity[] newArray(int size) {
            return new AlbumEntity[size];
        }
    };
}
