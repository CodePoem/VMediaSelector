package com.vdreamers.vmediaselector;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.vdreamers.vmediaselector.core.callback.MediaSelectCallback;
import com.vdreamers.vmediaselector.core.callback.MediaSelectUriCallback;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorModeConstants;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.IMediaSelector;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;

import java.util.ArrayList;

/**
 * 多媒体选择器工具类
 * <p>
 * date 2019/09/18 20:26:23
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MediaSelectorUtils {
    /**
     * requestCode-多媒体选择器
     */
    public static final int REQUEST_CODE_MEDIA_SELECTOR = 8023;
    /**
     * 图片是否多选
     */
    private boolean mImageMultiSelected = SelectorOptions.DEFAULT_MULTI_SELECTABLE;
    /**
     * 图片多选最大数量
     */
    private int mImageMultiMaxNum = SelectorOptions.DEFAULT_MAX_SELECT_NUM;
    /**
     * 是否需要相机
     */
    private boolean mNeedCamera = SelectorOptions.DEFAULT_NEED_CAMERA;
    /**
     * 请求码
     */
    private int mRequestCodeSelector = REQUEST_CODE_MEDIA_SELECTOR;
    /**
     * 多媒体选择器
     */
    private IMediaSelector mMediaSelector;
    /**
     * 选择器参数选项参数
     */
    private SelectorOptions mSelectorOptions;

    private MediaSelectorUtils(IMediaSelector iMediaSelecto) {
        mMediaSelector = iMediaSelecto;
    }

    public static MediaSelectorUtils of(IMediaSelector iMediaSelector) {
        return new MediaSelectorUtils(iMediaSelector);
    }

    /**
     * 选择图片
     *
     * @param fragment            androidx Fragment调用方
     * @param mediaSelectCallback 多媒体选择回调
     */
    public void selectImage(Fragment fragment,
                            MediaSelectCallback mediaSelectCallback) {
        selectImage(fragment, mediaSelectCallback, null);
    }

    /**
     * 选择图片
     *
     * @param activity            activity调用方
     * @param mediaSelectCallback 多媒体选择回调
     */
    public void selectImage(Activity activity, MediaSelectCallback mediaSelectCallback) {
        selectImage(activity, mediaSelectCallback, null);
    }

    /**
     * 选择图片
     *
     * @param fragment            androidx Fragment调用方
     * @param mediaSelectCallback 多媒体选择回调
     * @param selectedMedias      已选择的多媒体列表
     */
    public void selectImage(Fragment fragment,
                            MediaSelectCallback mediaSelectCallback,
                            ArrayList<MediaEntity> selectedMedias) {
        SelectorOptions selectorOptions = createDefaultImageOptions();
        MediaSelector.of(fragment)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, selectedMedias, mediaSelectCallback);
    }

    /**
     * 选择图片
     *
     * @param activity            activity调用方
     * @param mediaSelectCallback 多媒体选择回调
     * @param selectedMedias      已选择的多媒体列表
     */
    public void selectImage(Activity activity, MediaSelectCallback mediaSelectCallback,
                            ArrayList<MediaEntity> selectedMedias) {
        SelectorOptions selectorOptions = createDefaultImageOptions();
        MediaSelector.of(activity)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, selectedMedias, mediaSelectCallback);
    }

    /**
     * 选择图片
     *
     * @param fragment               androidx Fragment调用方
     * @param mediaSelectUriCallback 多媒体选择回调（Uri形式）
     */
    public void selectImage(Fragment fragment,
                            MediaSelectUriCallback mediaSelectUriCallback) {
        selectImage(fragment, mediaSelectUriCallback, null);
    }

    /**
     * 选择图片
     *
     * @param activity               activity调用方
     * @param mediaSelectUriCallback 多媒体选择回调（Uri形式）
     */
    public void selectImage(Activity activity, MediaSelectUriCallback mediaSelectUriCallback) {
        selectImage(activity, mediaSelectUriCallback, null);
    }

    /**
     * 选择图片
     *
     * @param fragment               androidx Fragment调用方
     * @param mediaSelectUriCallback 多媒体选择回调（Uri形式）
     * @param selectedMedias         已选择的多媒体列表
     */
    public void selectImage(Fragment fragment,
                            MediaSelectUriCallback mediaSelectUriCallback,
                            ArrayList<MediaEntity> selectedMedias) {
        SelectorOptions selectorOptions = createDefaultImageOptions();
        MediaSelector.of(fragment)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, selectedMedias, mediaSelectUriCallback);
    }

    /**
     * 选择图片
     *
     * @param activity               activity调用方
     * @param mediaSelectUriCallback 多媒体选择回调（Uri形式）
     * @param selectedMedias         已选择的多媒体列表
     */
    public void selectImage(Activity activity, MediaSelectUriCallback mediaSelectUriCallback,
                            ArrayList<MediaEntity> selectedMedias) {
        SelectorOptions selectorOptions = createDefaultImageOptions();
        MediaSelector.of(activity)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, selectedMedias, mediaSelectUriCallback);
    }

    @NonNull
    private SelectorOptions createDefaultImageOptions() {
        return SelectorOptions.of()
                .setMultiSelectable(mImageMultiSelected)
                .setMaxSelectNum(mImageMultiMaxNum)
                .setNeedCamera(mNeedCamera);
    }

    /**
     * 选择视频
     *
     * @param activity            activity调用方
     * @param mediaSelectCallback 多媒体选择回调
     */
    public void selectVideo(Activity activity, MediaSelectCallback mediaSelectCallback) {
        SelectorOptions selectorOptions = createDefaultVideoOptions();
        MediaSelector.of(activity)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, mediaSelectCallback);
    }

    /**
     * 选择视频
     *
     * @param fragment            androidx Fragment调用方
     * @param mediaSelectCallback 多媒体选择回调
     */
    public void selectVideo(Fragment fragment,
                            MediaSelectCallback mediaSelectCallback) {
        SelectorOptions selectorOptions = createDefaultVideoOptions();
        MediaSelector.of(fragment)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, mediaSelectCallback);
    }

    /**
     * 选择视频
     *
     * @param activity               activity调用方
     * @param mediaSelectUriCallback 多媒体选择回调（Uri形式）
     */
    public void selectVideo(Activity activity, MediaSelectUriCallback mediaSelectUriCallback) {
        SelectorOptions selectorOptions = createDefaultVideoOptions();
        MediaSelector.of(activity)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, mediaSelectUriCallback);
    }

    /**
     * 选择视频
     *
     * @param fragment               androidx Fragment调用方
     * @param mediaSelectUriCallback 多媒体选择回调（Uri形式）
     */
    public void selectVideo(Fragment fragment,
                            MediaSelectUriCallback mediaSelectUriCallback) {
        SelectorOptions selectorOptions = createDefaultVideoOptions();
        MediaSelector.of(fragment)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, mediaSelectUriCallback);
    }

    @NonNull
    private SelectorOptions createDefaultVideoOptions() {
        return SelectorOptions.of()
                .setMode(SelectorModeConstants.MODE_VIDEO)
                .setMultiSelectable(false);
    }

    /**
     * 设置图片是否可多选
     *
     * @param imageMultiSelected 图片是否可多选
     */
    public MediaSelectorUtils setImageMultiSelected(boolean imageMultiSelected) {
        mImageMultiSelected = imageMultiSelected;
        return this;
    }

    /**
     * 设置图片多选最大数量（在图片可多选情况下生效）
     *
     * @param imageMultiMaxNum 图片多选最大数量
     */
    public MediaSelectorUtils setImageMultiMaxNum(int imageMultiMaxNum) {
        this.mImageMultiMaxNum = imageMultiMaxNum;
        return this;
    }

    /**
     * 设置是否需要相机
     * @param needCamera 是否需要相机
     * @return
     */
    public MediaSelectorUtils setNeedCamera(boolean needCamera) {
        mNeedCamera = needCamera;
        return this;
    }
}
