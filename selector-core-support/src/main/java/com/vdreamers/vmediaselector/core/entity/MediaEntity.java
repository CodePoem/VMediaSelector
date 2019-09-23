package com.vdreamers.vmediaselector.core.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.vdreamers.vmediaselector.core.scope.MediaType;

import java.util.Locale;


/**
 * 多媒体实体类
 * <p>
 * date 2019-09-18 20:24:17
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class MediaEntity implements Parcelable {

    /**
     * 多媒体类型 {@link com.vdreamers.vmediaselector.core.scope.MediaTypeConstants}
     */
    @MediaType
    protected int mType;
    /**
     * Uri
     */
    protected Uri mUri;
    /**
     * Id
     */
    protected String mId;
    /**
     * 大小
     */
    protected String mSize;
    /**
     * 是否被选中
     */
    protected boolean mIsSelected;
    /**
     * 单位MB byte数
     */
    protected static final long MB = 1024 * 1024;

    public MediaEntity() {
    }

    public int getType() {
        return mType;
    }

    public MediaEntity setType(@MediaType int type) {
        this.mType = type;
        return this;
    }

    public Uri getUri() {
        return mUri;
    }

    public MediaEntity setUri(Uri uri) {
        mUri = uri;
        return this;
    }

    public String getId() {
        return mId;
    }

    public MediaEntity setId(String id) {
        mId = id;
        return this;
    }

    public MediaEntity setSize(String size) {
        mSize = size;
        return this;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public MediaEntity setSelected(boolean selected) {
        mIsSelected = selected;
        return this;
    }

    public long getSize() {
        try {
            long result = Long.parseLong(mSize);
            return result > 0 ? result : 0;
        } catch (NumberFormatException size) {
            return 0;
        }
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
        dest.writeInt(this.mType);
        dest.writeParcelable(this.mUri, flags);
        dest.writeString(this.mId);
        dest.writeString(this.mSize);
        dest.writeByte(this.mIsSelected ? (byte) 1 : (byte) 0);
    }

    protected MediaEntity(Parcel in) {
        this.mType = in.readInt();
        this.mUri = in.readParcelable(Uri.class.getClassLoader());
        this.mId = in.readString();
        this.mSize = in.readString();
        this.mIsSelected = in.readByte() != 0;
    }
}
