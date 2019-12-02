package com.vdreamers.vmediaselector.core.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.vdreamers.vmediaselector.core.scope.ImageMimeType;
import com.vdreamers.vmediaselector.core.scope.ImageMimeTypeConstants;
import com.vdreamers.vmediaselector.core.scope.MediaType;
import com.vdreamers.vmediaselector.core.scope.MediaTypeConstants;

import java.io.File;


/**
 * 图片多媒体实体类
 * <p>
 * date 2019-09-18 20:24:12
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ImageMediaEntity extends MediaEntity implements Parcelable {

    /**
     * 缩略图路径Uri
     */
    private Uri mThumbnailUri;
    /**
     * 图片高度
     */
    private int mHeight;
    /**
     * 图片宽度
     */
    private int mWidth;
    /**
     * 媒体类型
     */
    @ImageMimeType
    private String mMimeType;

    {
        this.mType = MediaTypeConstants.MEDIA_TYPE_IMAGE;
    }

    public ImageMediaEntity() {
    }

    public ImageMediaEntity(@NonNull File file, Uri uri) {
        this.mTitle = file.getName();
        this.mUri = uri;
        this.mSize = String.valueOf(file.length());
        this.mIsSelected = true;
    }

    public static ImageMediaEntity of() {
        return new ImageMediaEntity();
    }

    public Uri getThumbnailUri() {
        return mThumbnailUri;
    }

    public ImageMediaEntity setThumbnailUri(Uri thumbnailUri) {
        mThumbnailUri = thumbnailUri;
        return this;
    }

    public int getHeight() {
        return mHeight;
    }

    public ImageMediaEntity setHeight(int height) {
        mHeight = height;
        return this;
    }

    public int getWidth() {
        return mWidth;
    }

    public ImageMediaEntity setWidth(int width) {
        mWidth = width;
        return this;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public ImageMediaEntity setMimeType(@ImageMimeType String mimeType) {
        mMimeType = mimeType;
        return this;
    }

    @Override
    public ImageMediaEntity setType(@MediaType int type) {
        this.mType = type;
        return this;
    }

    @Override
    public ImageMediaEntity setUri(Uri uri) {
        mUri = uri;
        return this;
    }

    @Override
    public ImageMediaEntity setId(long id) {
        mId = id;
        return this;
    }

    @Override
    public ImageMediaEntity setTitle(String title) {
        mTitle = title;
        return this;
    }

    @Override
    public ImageMediaEntity setSize(String size) {
        mSize = size;
        return this;
    }

    public boolean isPng() {
        return ImageMimeTypeConstants.IMAGE_PNG.equals(getMimeType());
    }

    public boolean isJpg() {
        return ImageMimeTypeConstants.IMAGE_JPG.equals(getMimeType()) || ImageMimeTypeConstants.IMAGE_JPEG.equals(getMimeType());
    }

    public boolean isGif() {
        return ImageMimeTypeConstants.IMAGE_GIF.equals(getMimeType());
    }

    @Override
    public String toString() {
        return "ImageMediaEntity{" +
                ", mSize='" + mSize + '\'' +
                ", mHeight=" + mHeight + '\'' +
                ", mWidth=" + mWidth + "}";
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(mId).hashCode();
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageMediaEntity other = (ImageMediaEntity) obj;
        return this.mId == other.mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mThumbnailUri, flags);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mWidth);
        dest.writeString(this.mMimeType);
    }

    protected ImageMediaEntity(Parcel in) {
        super(in);
        this.mThumbnailUri = in.readParcelable(Uri.class.getClassLoader());
        this.mHeight = in.readInt();
        this.mWidth = in.readInt();
        this.mMimeType = in.readString();
    }

    public static final Creator<ImageMediaEntity> CREATOR = new Creator<ImageMediaEntity>() {
        @Override
        public ImageMediaEntity createFromParcel(Parcel source) {
            return new ImageMediaEntity(source);
        }

        @Override
        public ImageMediaEntity[] newArray(int size) {
            return new ImageMediaEntity[size];
        }
    };
}
