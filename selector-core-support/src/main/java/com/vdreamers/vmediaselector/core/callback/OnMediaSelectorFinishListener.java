package com.vdreamers.vmediaselector.core.callback;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;

import java.util.List;

/**
 * 多媒体选择器选择结束监听器
 * <p>
 * date 2019-09-18 20:23:50
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface OnMediaSelectorFinishListener {

    /**
     * 多媒体选择器选择结束
     * {@link com.vdreamers.vmediaselector.core.contract.SelectorContract.View#onFinish(List)}
     *
     * @param intent result Intent 包含结果的Intent
     * @param medias the selection of medias. 选择的多媒体列表
     */
    void onMediaSelectorFinish(Intent intent, @Nullable List<MediaEntity> medias);
}
