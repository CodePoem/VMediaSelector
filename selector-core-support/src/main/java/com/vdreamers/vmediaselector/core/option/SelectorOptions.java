package com.vdreamers.vmediaselector.core.option;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * 多媒体选择器选项参数
 * <p>
 * date 2019/09/18 20:24:52
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SelectorOptions implements Parcelable {

    /**
     * 选择模式 {@link SelectorModeConstants}
     */
    @SelectorMode
    private int mMode = SelectorModeConstants.MODE_IMAGE;
    /**
     * 预览模式 {@link SelectorViewModeConstants}
     */
    @SelectorViewMode
    private int mViewMode = SelectorViewModeConstants.VIEW_MODE_PREVIEW;

    /**
     * 是否可多选
     */
    private boolean mMultiSelectable = DEFAULT_MULTI_SELECTABLE;
    /**
     * 最大选择数量
     */
    private int mMaxSelectNum = DEFAULT_MAX_SELECT_NUM;
    /**
     * 是否需要相机
     */
    private boolean mNeedCamera = DEFAULT_NEED_CAMERA;
    /**
     * 是否支持Gif
     */
    private boolean mNeedGif = DEFAULT_NEED_GIF;
    /**
     * 是否需要分页
     */
    private boolean mNeedPaging = DEFAULT_NEED_PAGING;
    /**
     * 是否存储拍照后的图片
     */
    private boolean mStoreCameraImage = DEFAULT_STORE_CAMERA_IMAGE;
    /**
     * FileProvider 授权标识
     */
    private String mAuthorities;
    /**
     * 子目录-用于存储拍照等 如果改变必须自行适配7.0 authorities
     */
    private String mSubDir;
    /**
     * 默认多选
     */
    public static final boolean DEFAULT_MULTI_SELECTABLE = true;
    /**
     * 默认最大选择数量9
     */
    public static final int DEFAULT_MAX_SELECT_NUM = 9;
    /**
     * 默认需要拍照
     */
    public static final boolean DEFAULT_NEED_CAMERA = true;
    /**
     * 默认支持Gif
     */
    public static final boolean DEFAULT_NEED_GIF = true;
    /**
     * 默认分页
     */
    public static final boolean DEFAULT_NEED_PAGING = true;
    /**
     * 默认存储拍照后的图片
     */
    public static final boolean DEFAULT_STORE_CAMERA_IMAGE = true;

    /**
     * 多媒体占位图资源Id
     */
    @DrawableRes
    private int mMediaPlaceHolderRes;
    /**
     * 多媒体选中图资源Id
     */
    @DrawableRes
    private int mMediaCheckedRes;
    /**
     * 多媒体未选中图资源Id
     */
    @DrawableRes
    private int mMediaUnCheckedRes;
    /**
     * 相册占位图资源Id
     */
    @DrawableRes
    private int mAlbumPlaceHolderRes;
    /**
     * 视频图标资源Id
     */
    @DrawableRes
    private int mVideoDurationRes;
    /**
     * 拍照图资源Id
     */
    @DrawableRes
    private int mCameraRes;

    private SelectorOptions() {
    }

    public static SelectorOptions getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final SelectorOptions INSTANCE = new SelectorOptions();
    }

    public static SelectorOptions getCleanInstance() {
        SelectorOptions selectorOptions = getInstance();
        selectorOptions.reset();
        return selectorOptions;
    }

    private void reset() {
        mMode = SelectorModeConstants.MODE_IMAGE;
        mMultiSelectable = false;
        mMaxSelectNum = DEFAULT_MAX_SELECT_NUM;
        mNeedCamera = false;
        mNeedGif = false;
        mNeedPaging = true;
    }

    public static SelectorOptions of() {
        return getInstance();
    }

    public boolean isMultiSelectable() {
        return mMultiSelectable;
    }

    public SelectorOptions setMultiSelectable(boolean multiSelectable) {
        mMultiSelectable = multiSelectable;
        return this;
    }

    public int getMaxSelectNum() {
        return mMaxSelectNum;
    }

    public SelectorOptions setMaxSelectNum(int maxSelectNum) {
        mMaxSelectNum = maxSelectNum;
        return this;
    }

    public boolean isNeedCamera() {
        return mNeedCamera;
    }

    public SelectorOptions setNeedCamera(boolean needCamera) {
        mNeedCamera = needCamera;
        return this;
    }

    public boolean isNeedGif() {
        return mNeedGif;
    }

    public SelectorOptions setNeedGif(boolean needGif) {
        mNeedGif = needGif;
        return this;
    }

    public int getMode() {
        return mMode;
    }

    public SelectorOptions setMode(@SelectorMode int mode) {
        mMode = mode;
        return this;
    }

    public int getViewMode() {
        return mViewMode;
    }

    public SelectorOptions setViewMode(int viewMode) {
        mViewMode = viewMode;
        return this;
    }

    public boolean isNeedPaging() {
        return mNeedPaging;
    }

    public SelectorOptions setNeedPaging(boolean needPaging) {
        mNeedPaging = needPaging;
        return this;
    }

    public @DrawableRes
    int getMediaPlaceHolderRes() {
        return mMediaPlaceHolderRes;
    }

    public SelectorOptions setMediaPlaceHolderRes(@DrawableRes int mediaPlaceHolderRes) {
        mMediaPlaceHolderRes = mediaPlaceHolderRes;
        return this;
    }

    public @DrawableRes
    int getMediaCheckedRes() {
        return mMediaCheckedRes;
    }

    public SelectorOptions setMediaCheckedRes(@DrawableRes int mediaCheckedRes) {
        mMediaCheckedRes = mediaCheckedRes;
        return this;
    }

    public @DrawableRes
    int getMediaUnCheckedRes() {
        return mMediaUnCheckedRes;
    }

    public SelectorOptions setMediaUnCheckedRes(@DrawableRes int mediaUnCheckedRes) {
        mMediaUnCheckedRes = mediaUnCheckedRes;
        return this;
    }

    public @DrawableRes
    int getAlbumPlaceHolderRes() {
        return mAlbumPlaceHolderRes;
    }

    public SelectorOptions setAlbumPlaceHolderRes(@DrawableRes int albumPlaceHolderRes) {
        mAlbumPlaceHolderRes = albumPlaceHolderRes;
        return this;
    }

    public @DrawableRes
    int getVideoDurationRes() {
        return mVideoDurationRes;
    }

    public SelectorOptions setVideoDurationRes(@DrawableRes int videoDurationRes) {
        mVideoDurationRes = videoDurationRes;
        return this;
    }

    public @DrawableRes
    int getCameraRes() {
        return mCameraRes;
    }

    public SelectorOptions setCameraRes(@DrawableRes int cameraRes) {
        mCameraRes = cameraRes;
        return this;
    }

    public boolean isStoreCameraImage() {
        return mStoreCameraImage;
    }

    public SelectorOptions setStoreCameraImage(boolean storeCameraImage) {
        mStoreCameraImage = storeCameraImage;
        return this;
    }

    public String getAuthorities() {
        return mAuthorities;
    }

    public SelectorOptions setAuthorities(String authorities) {
        this.mAuthorities = authorities;
        return this;
    }

    public String getSubDir() {
        return mSubDir;
    }

    public SelectorOptions setSubDir(String subDir) {
        mSubDir = subDir;
        return this;
    }

    public boolean isNeedLoading() {
        return mViewMode == SelectorViewModeConstants.VIEW_MODE_EDIT;
    }

    public boolean isNeedEdit() {
        return mViewMode != SelectorViewModeConstants.VIEW_MODE_PREVIEW;
    }

    public boolean isImageMode() {
        return SelectorModeConstants.MODE_IMAGE == mMode;
    }

    public boolean isVideoMode() {
        return SelectorModeConstants.MODE_VIDEO == mMode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMode);
        dest.writeInt(this.mViewMode);
        dest.writeByte(this.mMultiSelectable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mMaxSelectNum);
        dest.writeByte(this.mNeedCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNeedGif ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNeedPaging ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mStoreCameraImage ? (byte) 1 : (byte) 0);
        dest.writeString(this.mAuthorities);
        dest.writeString(this.mSubDir);
        dest.writeInt(this.mMediaPlaceHolderRes);
        dest.writeInt(this.mMediaCheckedRes);
        dest.writeInt(this.mMediaUnCheckedRes);
        dest.writeInt(this.mAlbumPlaceHolderRes);
        dest.writeInt(this.mVideoDurationRes);
        dest.writeInt(this.mCameraRes);
    }

    protected SelectorOptions(Parcel in) {
        this.mMode = in.readInt();
        this.mViewMode = in.readInt();
        this.mMultiSelectable = in.readByte() != 0;
        this.mMaxSelectNum = in.readInt();
        this.mNeedCamera = in.readByte() != 0;
        this.mNeedGif = in.readByte() != 0;
        this.mNeedPaging = in.readByte() != 0;
        this.mStoreCameraImage = in.readByte() != 0;
        this.mAuthorities = in.readString();
        this.mSubDir = in.readString();
        this.mMediaPlaceHolderRes = in.readInt();
        this.mMediaCheckedRes = in.readInt();
        this.mMediaUnCheckedRes = in.readInt();
        this.mAlbumPlaceHolderRes = in.readInt();
        this.mVideoDurationRes = in.readInt();
        this.mCameraRes = in.readInt();
    }

    public static final Creator<SelectorOptions> CREATOR = new Creator<SelectorOptions>() {
        @Override
        public SelectorOptions createFromParcel(Parcel source) {
            return new SelectorOptions(source);
        }

        @Override
        public SelectorOptions[] newArray(int size) {
            return new SelectorOptions[size];
        }
    };
}
