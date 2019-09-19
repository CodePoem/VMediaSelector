package com.vdreamers.vmediaselector.core.callback;

import android.content.Intent;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;

import java.util.List;

/**
 * 多媒体选择回调
 * <p>
 * date 2019-09-18 20:23:39
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface MediaSelectCallback {

    /**
     * 多媒体选择成功回调
     *
     * @param medias 多媒体选择结果
     * @param resultCode 结果码
     * @param data 包含多媒体选择结果Intent
     */
    void onMediaSelectSuccess(int resultCode, Intent data, List<MediaEntity> medias);

    /**
     * 多媒体选择异常回调
     *
     * @param mediaSelectError 多媒体选择异常
     */
    void onMediaSelectError(Throwable mediaSelectError);
}
