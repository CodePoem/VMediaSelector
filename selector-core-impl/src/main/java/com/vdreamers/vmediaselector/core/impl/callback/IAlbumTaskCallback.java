package com.vdreamers.vmediaselector.core.impl.callback;


import androidx.annotation.Nullable;

import com.vdreamers.vmediaselector.core.entity.AlbumEntity;

import java.util.List;

/**
 * 相册加载任务回调
 * <p>
 * date 2019-09-18 20:31:35
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface IAlbumTaskCallback {

    /**
     * 获取数据库中所有相册列表
     *
     * @param albumEntityList 数据库中所有相册列表
     */
    void postAlbumList(@Nullable List<AlbumEntity> albumEntityList);

}
