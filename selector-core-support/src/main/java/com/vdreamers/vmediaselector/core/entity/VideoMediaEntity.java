package com.vdreamers.vmediaselector.core.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.vdreamers.vmediaselector.core.scope.MediaType;

import java.util.Locale;

/**
 * 视频多媒体实体类
 * <p>
 * date 2019-09-18 20:24:21
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class VideoMediaEntity extends MediaEntity implements Parcelable {

    /**
     * 标题
     */
    private String mTitle;
    /**
     * 时长
     */
    private String mDuration;
    /**
     * 拍摄时间
     */
    private String mDateTaken;
    /**
     * 媒体类型
     */
    private String mMimeType;
    /**
     * 单位MB byte数
     */
    private static final long MB = 1024 * 1024;

    public VideoMediaEntity() {
    }

    public static VideoMediaEntity of() {
        return new VideoMediaEntity();
    }

    public String getTitle() {
        return mTitle;
    }

    public VideoMediaEntity setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getDuration() {
        return mDuration;
    }

    public VideoMediaEntity setDuration(String duration) {
        mDuration = duration;
        return this;
    }

    public String getDateTaken() {
        return mDateTaken;
    }

    public VideoMediaEntity setDateTaken(String dateTaken) {
        mDateTaken = dateTaken;
        return this;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public VideoMediaEntity setMimeType(String mimeType) {
        mMimeType = mimeType;
        return this;
    }

    @Override
    public VideoMediaEntity setType(@MediaType int type) {
        this.type = type;
        return this;
    }

    @Override
    public VideoMediaEntity setUri(Uri uri) {
        mUri = uri;
        return this;
    }

    @Override
    public VideoMediaEntity setId(String id) {
        mId = id;
        return this;
    }

    @Override
    public VideoMediaEntity setSize(String size) {
        mSize = size;
        return this;
    }

    public String getSizeByUnit() {
        double size = getSize();
        if (size == 0) {
            return "0K";
        }
        if (size >= MB) {
            double sizeInMb = size / MB;
            return String.format(Locale.getDefault(), "%.1f", sizeInMb) + "M";
        }
        double sizeInKb = size / 1024;
        return String.format(Locale.getDefault(), "%.1f", sizeInKb) + "K";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDuration);
        dest.writeString(this.mDateTaken);
        dest.writeString(this.mMimeType);
    }

    protected VideoMediaEntity(Parcel in) {
        super(in);
        this.mTitle = in.readString();
        this.mDuration = in.readString();
        this.mDateTaken = in.readString();
        this.mMimeType = in.readString();
    }

    public static final Creator<VideoMediaEntity> CREATOR = new Creator<VideoMediaEntity>() {
        @Override
        public VideoMediaEntity createFromParcel(Parcel source) {
            return new VideoMediaEntity(source);
        }

        @Override
        public VideoMediaEntity[] newArray(int size) {
            return new VideoMediaEntity[size];
        }
    };
}
