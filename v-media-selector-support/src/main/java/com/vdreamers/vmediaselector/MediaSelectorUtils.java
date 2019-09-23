package com.vdreamers.vmediaselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.vdreamers.vmediaselector.core.callback.MediaSelectCallback;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorModeConstants;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.IMediaSelector;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;
import com.vdreamers.vmediaselector.obtain.ObtainListener;
import com.vdreamers.vmediaselector.obtain.ObtainUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
     * 是否支持Gif
     */
    private boolean mNeedGif = SelectorOptions.DEFAULT_NEED_GIF;
    /**
     * FileProvider 授权标识
     */
    private String mAuthorities;
    /**
     * 子目录-用于存储拍照等 如果改变必须自行适配7.0 authorities
     */
    private String mSubDir;
    /**
     * 请求码
     */
    private int mRequestCodeSelector = REQUEST_CODE_MEDIA_SELECTOR;
    /**
     * 多媒体选择器
     */
    private IMediaSelector mMediaSelector;

    private MediaSelectorUtils(IMediaSelector iMediaSelecto) {
        mMediaSelector = iMediaSelecto;
    }

    public static MediaSelectorUtils of(IMediaSelector iMediaSelector) {
        return new MediaSelectorUtils(iMediaSelector);
    }

    /**
     * 选择图片
     *
     * @param fragment                 androidx Fragment调用方
     * @param mediaSelectFilesCallback 多媒体选择回调
     */
    public void selectImage(Fragment fragment,
                            MediaSelectFilesCallback mediaSelectFilesCallback) {
        selectImage(fragment, null, mediaSelectFilesCallback);
    }

    /**
     * 选择图片
     *
     * @param activity                 activity调用方
     * @param mediaSelectFilesCallback 多媒体选择回调
     */
    public void selectImage(Activity activity, MediaSelectFilesCallback mediaSelectFilesCallback) {
        selectImage(activity, null, mediaSelectFilesCallback);
    }

    /**
     * 选择图片 附带已选择图片
     *
     * @param fragment                 androidx Fragment调用方
     * @param selectedUris             已选择的多媒体Uri列表
     * @param mediaSelectFilesCallback 多媒体选择回调
     */
    public void selectImage(final Fragment fragment, ArrayList<Uri> selectedUris,
                            final MediaSelectFilesCallback mediaSelectFilesCallback) {
        if (fragment == null) {
            return;
        }
        ArrayList<MediaEntity> selectedMedias = findSelectMedias(selectedUris);
        SelectorOptions selectorOptions = createDefaultImageOptions();
        MediaSelector.of(fragment)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, selectedMedias, new MediaSelectCallback() {
                    @Override
                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                     List<MediaEntity> medias) {
                        saveSelectedMediaEntity(medias);
                        handleMediaSelectSuccess(fragment.getActivity(), resultCode, data,
                                medias,
                                mediaSelectFilesCallback);
                    }

                    @Override
                    public void onMediaSelectError(Throwable mediaSelectError) {
                        handleonMediaSelectError(mediaSelectError, mediaSelectFilesCallback);
                    }
                });
    }

    /**
     * 选择图片 附带已选择图片
     *
     * @param activity                 activity调用方
     * @param selectedUris             已选择的多媒体列表
     * @param mediaSelectFilesCallback 多媒体选择回调
     */
    public void selectImage(final Activity activity, ArrayList<Uri> selectedUris,
                            final MediaSelectFilesCallback mediaSelectFilesCallback) {
        if (activity == null) {
            return;
        }
        ArrayList<MediaEntity> selectedMedias = findSelectMedias(selectedUris);
        SelectorOptions selectorOptions = createDefaultImageOptions();
        MediaSelector.of(activity)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, selectedMedias, new MediaSelectCallback() {
                    @Override
                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                     List<MediaEntity> medias) {
                        saveSelectedMediaEntity(medias);
                        handleMediaSelectSuccess(activity, resultCode, data, medias,
                                mediaSelectFilesCallback);
                    }

                    @Override
                    public void onMediaSelectError(Throwable mediaSelectError) {
                        handleonMediaSelectError(mediaSelectError, mediaSelectFilesCallback);
                    }
                });
    }


    private ArrayList<MediaEntity> findSelectMedias(ArrayList<Uri> uris) {
        if (uris == null) {
            return null;
        }
        LinkedHashMap<Uri, MediaEntity> selectedMediaEntity =
                SelectedHolder.getInstance().getSelectedMediaEntity();
        if (selectedMediaEntity == null) {
            return null;
        }
        ArrayList<MediaEntity> selectedMedias = new ArrayList<>();
        for (Uri uri : uris) {
            if (selectedMediaEntity.containsKey(uri)) {
                selectedMedias.add(selectedMediaEntity.get(uri));
            }
        }
        return selectedMedias;
    }

    private void saveSelectedMediaEntity(List<MediaEntity> medias) {
        if (medias == null) {
            return;
        }
        LinkedHashMap<Uri, MediaEntity> selectedMediaEntity = new LinkedHashMap<>();
        for (MediaEntity media : medias) {
            if (media == null) {
                continue;
            }
            selectedMediaEntity.put(media.getUri(), media);
        }
        SelectedHolder.getInstance().setSelectedMediaEntity(selectedMediaEntity);
    }


    @NonNull
    private SelectorOptions createDefaultImageOptions() {
        return SelectorOptions.of()
                .setMode(SelectorModeConstants.MODE_IMAGE)
                .setMultiSelectable(mImageMultiSelected)
                .setMaxSelectNum(mImageMultiMaxNum)
                .setNeedCamera(mNeedCamera)
                .setNeedGif(mNeedGif)
                .setSubDir(mSubDir)
                .setAuthorities(mAuthorities);
    }

    /**
     * 选择视频
     *
     * @param activity                 activity调用方
     * @param mediaSelectFilesCallback 多媒体选择回调
     */
    public void selectVideo(final Activity activity,
                            final MediaSelectFilesCallback mediaSelectFilesCallback) {
        if (activity == null) {
            return;
        }
        SelectorOptions selectorOptions = createDefaultVideoOptions();
        MediaSelector.of(activity)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, new MediaSelectCallback() {
                    @Override
                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                     List<MediaEntity> medias) {
                        handleMediaSelectSuccess(activity, resultCode, data, medias,
                                mediaSelectFilesCallback);
                    }

                    @Override
                    public void onMediaSelectError(Throwable mediaSelectError) {
                        handleonMediaSelectError(mediaSelectError, mediaSelectFilesCallback);
                    }
                });
    }

    /**
     * 选择视频
     *
     * @param fragment                 androidx Fragment调用方
     * @param mediaSelectFilesCallback 多媒体选择回调
     */
    public void selectVideo(final Fragment fragment,
                            final MediaSelectFilesCallback mediaSelectFilesCallback) {
        if (fragment == null) {
            return;
        }
        SelectorOptions selectorOptions = createDefaultVideoOptions();
        MediaSelector.of(fragment)
                .withSelector(mMediaSelector)
                .withOptions(selectorOptions)
                .start(mRequestCodeSelector, new MediaSelectCallback() {
                    @Override
                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                     List<MediaEntity> medias) {
                        handleMediaSelectSuccess(fragment.getActivity(), resultCode, data,
                                medias,
                                mediaSelectFilesCallback);
                    }

                    @Override
                    public void onMediaSelectError(Throwable mediaSelectError) {
                        handleonMediaSelectError(mediaSelectError, mediaSelectFilesCallback);
                    }
                });
    }

    @NonNull
    private SelectorOptions createDefaultVideoOptions() {
        return SelectorOptions.of()
                .setMode(SelectorModeConstants.MODE_VIDEO)
                .setMultiSelectable(mImageMultiSelected)
                .setMaxSelectNum(mImageMultiMaxNum)
                .setNeedCamera(false);
    }

    private void handleMediaSelectSuccess(Context context, final int resultCode,
                                          final Intent data,
                                          List<MediaEntity> medias,
                                          final MediaSelectFilesCallback mediaSelectFilesCallback) {
        if (context == null) {
            return;
        }
        if (mediaSelectFilesCallback != null) {
            final List<Uri> uris = new ArrayList<>();
            for (MediaEntity mediaEntity : medias) {
                if (mediaEntity == null) {
                    continue;
                }
                uris.add(mediaEntity.getUri());
            }
            ObtainUtils.of()
                    .setCallback(new ObtainListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(List<File> files) {
                            mediaSelectFilesCallback.onSuccess(resultCode, data, uris, files);
                        }

                        @Override
                        public void onFailed(Throwable throwable) {
                            mediaSelectFilesCallback.onFailed(throwable);
                        }
                    }).obtain(context, uris);
        }
    }

    private void handleonMediaSelectError(Throwable mediaSelectError,
                                          MediaSelectFilesCallback mediaSelectFilesCallback) {
        if (mediaSelectFilesCallback != null) {
            mediaSelectFilesCallback.onFailed(mediaSelectError);
        }
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
     *
     * @param needCamera 是否需要相机
     */
    public MediaSelectorUtils setNeedCamera(boolean needCamera) {
        mNeedCamera = needCamera;
        return this;
    }

    public MediaSelectorUtils setNeedGif(boolean needGif) {
        mNeedGif = needGif;
        return this;
    }

    public MediaSelectorUtils setAuthorities(String authorities) {
        mAuthorities = authorities;
        return this;
    }

    public MediaSelectorUtils setSubDir(String subDir) {
        mSubDir = subDir;
        return this;
    }

    public MediaSelectorUtils setRequestCodeSelector(int requestCodeSelector) {
        mRequestCodeSelector = requestCodeSelector;
        return this;
    }
}
