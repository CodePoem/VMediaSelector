package com.vdreamers.vmediaselector.core.entity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.vdreamers.vmediaselector.core.scope.ImageType;
import com.vdreamers.vmediaselector.core.scope.ImageTypeConstants;
import com.vdreamers.vmediaselector.core.scope.MediaType;
import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;
import com.vdreamers.vmediaselector.core.utils.UriFileUtils;

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
     * 是否被选中
     */
    private boolean mIsSelected;
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
     * 图片类型
     */
    @ImageType
    private int mImageType;
    /**
     * 媒体类型
     */
    private String mMimeType;
    /**
     * Gif最大尺寸
     */
    private static final long MAX_GIF_SIZE = 1024 * 1024L;

    public ImageMediaEntity() {
    }

    public ImageMediaEntity(@NonNull File file) {
        this.mId = String.valueOf(System.currentTimeMillis());
        this.mSize = String.valueOf(file.length());
        this.mIsSelected = true;
    }

    public static ImageMediaEntity of() {
        return new ImageMediaEntity();
    }


    public boolean isSelected() {
        return mIsSelected;
    }

    public ImageMediaEntity setSelected(boolean selected) {
        mIsSelected = selected;
        return this;
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

    public int getImageType() {
        return mImageType;
    }

    public ImageMediaEntity setImageType(@ImageType int imageType) {
        mImageType = imageType;
        return this;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public ImageMediaEntity setMimeType(String mimeType) {
        mMimeType = mimeType;
        return this;
    }

    @Override
    public ImageMediaEntity setType(@MediaType int type) {
        this.type = type;
        return this;
    }

    @Override
    public ImageMediaEntity setUri(Uri uri) {
        mUri = uri;
        return this;
    }

    @Override
    public ImageMediaEntity setId(String id) {
        mId = id;
        return this;
    }

    @Override
    public ImageMediaEntity setSize(String size) {
        mSize = size;
        return this;
    }

    public boolean isGif() {
        return getImageType() == ImageTypeConstants.IMAGE_TYPE_GIF;
    }


    public boolean isGifOverSize() {
        return isGif() && getSize() > MAX_GIF_SIZE;
    }

    @Override
    public String toString() {
        return "ImageMediaEntity{" +
                ", mSize='" + mSize + '\'' +
                ", mHeight=" + mHeight +
                ", mWidth=" + mWidth;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
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
        return !(TextUtils.isEmpty(mId) || TextUtils.isEmpty(other.mId)) && this.mId.equals(other.mId);
    }

    /**
     * 保存图片到媒体存储
     * save image to MediaStore.
     */
    public void saveMediaStore(final Context context, final ContentResolver cr) {
        if (context == null) {
            return;
        }

        LoadExecutorUtils.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                if (cr != null && !TextUtils.isEmpty(getId())) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, getId());
                    values.put(MediaStore.Images.Media.MIME_TYPE, getMimeType());
                    File file = UriFileUtils.getFileFromUri(context, getUri());
                    if (file != null) {
                        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    }
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }
        });

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.mIsSelected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mThumbnailUri, flags);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mImageType);
        dest.writeString(this.mMimeType);
    }

    protected ImageMediaEntity(Parcel in) {
        super(in);
        this.mIsSelected = in.readByte() != 0;
        this.mThumbnailUri = in.readParcelable(Uri.class.getClassLoader());
        this.mHeight = in.readInt();
        this.mWidth = in.readInt();
        this.mImageType = in.readInt();
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
