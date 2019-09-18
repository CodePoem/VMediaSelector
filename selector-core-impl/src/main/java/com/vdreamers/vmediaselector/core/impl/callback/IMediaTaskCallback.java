package com.vdreamers.vmediaselector.core.impl.callback;


import androidx.annotation.Nullable;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;

import java.util.List;

/**
 * 多媒体加载任务回调 {@link MediaEntity}
 * <p>
 * date 2019-09-18 20:31:40
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface IMediaTaskCallback<T extends MediaEntity> {
    /**
     * 获取相册一页多媒体数据
     * get a page of medias in a album
     *
     * @param medias page of medias 相册一页多媒体数据列表
     * @param count  the count of the medias in album 相册中多媒体总数
     */
    void postMedia(@Nullable List<T> medias, int count);
}
