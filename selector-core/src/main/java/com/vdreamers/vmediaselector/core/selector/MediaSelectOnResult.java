package com.vdreamers.vmediaselector.core.selector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.vdreamers.vmediaselector.core.callback.MediaSelectCallback;
import com.vdreamers.vmediaselector.core.callback.MediaSelectUriCallback;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vonresult.core.OnResult;
import com.vdreamers.vonresult.core.OnResultCallBack;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 多媒体选择结果调用发起者
 * <p>
 * date 2019/09/18 20:26:33
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MediaSelectOnResult {

    private OnResult mOnResult;

    private MediaSelectOnResult(@NonNull final Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            mOnResult = OnResult.of(fragmentActivity);
        }
    }

    public MediaSelectOnResult(@NonNull final FragmentActivity activity) {
        mOnResult = OnResult.of(activity);
    }

    private MediaSelectOnResult(@NonNull final Fragment fragment) {
        mOnResult = OnResult.of(fragment);
    }

    public static MediaSelectOnResult of(@NonNull final Activity activity) {

        return new MediaSelectOnResult(activity);
    }

    public static MediaSelectOnResult of(@NonNull final FragmentActivity activity) {
        return new MediaSelectOnResult(activity);
    }

    public static MediaSelectOnResult of(@NonNull final Fragment fragment) {
        return new MediaSelectOnResult(fragment);
    }

    /**
     * 发起多媒体选择请求
     *
     * @param intent              跳转多媒体选择意图
     * @param requestCode         请求码
     * @param mediaSelectCallback 多媒体选择回调
     */
    public void start(Intent intent, int requestCode, MediaSelectCallback mediaSelectCallback,
                      MediaSelector mediaSelector) {
        OnMediaSelectOnResult onMediaSelectOnResult = new OnMediaSelectOnResult();
        onMediaSelectOnResult.setMediaSelectCallback(mediaSelectCallback);
        onMediaSelectOnResult.setMediaSelector(mediaSelector);
        mOnResult.start(intent, requestCode, onMediaSelectOnResult);
    }

    /**
     * 发起多媒体选择请求
     *
     * @param intent                 跳转多媒体选择意图
     * @param requestCode            请求码
     * @param mediaSelectUriCallback 多媒体选择回调
     */
    public void start(Intent intent, int requestCode,
                      MediaSelectUriCallback mediaSelectUriCallback, MediaSelector mediaSelector) {
        OnMediaSelectOnResult onMediaSelectOnResult = new OnMediaSelectOnResult();
        onMediaSelectOnResult.setMediaSelectUriCallback(mediaSelectUriCallback);
        onMediaSelectOnResult.setMediaSelector(mediaSelector);
        mOnResult.start(intent, requestCode, onMediaSelectOnResult);
    }

    @SuppressWarnings("WeakerAccess")
    private static class OnMediaSelectOnResult implements OnResultCallBack {

        /**
         * 多媒体选择回调
         */
        private MediaSelectCallback mMediaSelectCallback;
        /**
         * 多媒体选择回调（Uri形式）
         */
        private MediaSelectUriCallback mMediaSelectUriCallback;
        /**
         * 多媒体选择器
         */
        private MediaSelector mMediaSelector;

        public OnMediaSelectOnResult() {
        }


        public void setMediaSelectCallback(MediaSelectCallback mediaSelectCallback) {
            mMediaSelectCallback = mediaSelectCallback;
        }

        public void setMediaSelectUriCallback(MediaSelectUriCallback mediaSelectUriCallback) {
            mMediaSelectUriCallback = mediaSelectUriCallback;
        }

        public void setMediaSelector(MediaSelector mediaSelector) {
            mMediaSelector = mediaSelector;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                List<MediaEntity> mediaEntities = mMediaSelector.getResult(data);
                if (mediaEntities == null) {
                    if (mMediaSelectCallback != null) {
                        mMediaSelectCallback.onMediaSelectError(new Throwable("medias result is " +
                                "null"));
                    }
                    if (mMediaSelectUriCallback != null) {
                        mMediaSelectUriCallback.onMediaSelectError(new Throwable("medias result " +
                                "is " +
                                "null"));
                    }
                    return;
                }
                if (mMediaSelectCallback != null) {
                    mMediaSelectCallback.onMediaSelectSuccess(resultCode, data, mediaEntities);
                }
                if (mMediaSelectUriCallback != null) {
                    List<Uri> uris = new ArrayList<>();
                    for (MediaEntity mediaEntity : mediaEntities) {
                        if (mediaEntity == null) {
                            continue;
                        }
                        uris.add(mediaEntity.getUri());
                    }
                    mMediaSelectUriCallback.onMediaSelectSuccess(resultCode, data, uris);
                }
            }
        }
    }
}
