package com.vdreamers.vmediaselector.core.impl.task;

import android.content.ContentResolver;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.impl.callback.IMediaTaskCallback;


/**
 * 多媒体任务
 * <p>
 * date 2019-09-18 20:32:02
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface IMediaTask<T extends MediaEntity> {

    /**
     * 多媒体加载
     *
     * @param cr       内容解析器
     * @param page     页数
     * @param id       多媒体Id
     * @param callback 多媒体加载回调
     */
    void load(ContentResolver cr, int page, String id, IMediaTaskCallback<T> callback);

}
