package com.vdreamers.vmediaselector.core.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.vdreamers.vmediaselector.core.scope.MediaType;
import com.vdreamers.vmediaselector.core.scope.MediaTypeConstants;

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

    {
        this.mType = MediaTypeConstants.MEDIA_TYPE_VIDEO;
    }

    public VideoMediaEntity() {
    }

    public static VideoMediaEntity of() {
        return new VideoMediaEntity();
    }

    public String getDuration() {
        try {
            long duration = Long.parseLong(mDuration);
            return DateUtils.formatElapsedTime(duration / 1000);
        } catch (NumberFormatException e) {
            return "0:00";
        }
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
        this.mType = type;
        return this;
    }

    @Override
    public VideoMediaEntity setUri(Uri uri) {
        mUri = uri;
        return this;
    }

    @Override
    public VideoMediaEntity setId(long id) {
        mId = id;
        return this;
    }

    @Override
    public VideoMediaEntity setTitle(String title) {
        mTitle = title;
        return this;
    }

    @Override
    public VideoMediaEntity setSize(String size) {
        mSize = size;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mDuration);
        dest.writeString(this.mDateTaken);
        dest.writeString(this.mMimeType);
    }

    protected VideoMediaEntity(Parcel in) {
        super(in);
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
