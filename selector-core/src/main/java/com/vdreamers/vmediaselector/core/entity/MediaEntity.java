package com.vdreamers.vmediaselector.core.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.vdreamers.vmediaselector.core.scope.MediaType;


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
    protected int type;
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

    public MediaEntity() {
    }

    public int getType() {
        return type;
    }

    public MediaEntity setType(@MediaType int type) {
        this.type = type;
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

    public long getSize() {
        try {
            long result = Long.parseLong(mSize);
            return result > 0 ? result : 0;
        } catch (NumberFormatException size) {
            return 0;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.mUri, flags);
        dest.writeString(this.mId);
        dest.writeString(this.mSize);
    }

    protected MediaEntity(Parcel in) {
        this.type = in.readInt();
        this.mUri = in.readParcelable(Uri.class.getClassLoader());
        this.mId = in.readString();
        this.mSize = in.readString();
    }
}
