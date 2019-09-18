package com.vdreamers.vmediaselector.core.selector;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;

import java.util.List;

/**
 * 多媒体选择器接口
 * <p>
 * date 2019-09-18 20:25:36
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface IMediaSelector {

    /**
     * 多媒体选择器初始化
     * MediaSelector init
     */
    void init();

    /**
     * 应用多媒体选择器选择参数设置
     *
     * @param activity        activity调用方
     * @param fragment        fragment调用方
     * @param selectorOptions 多媒体选择器选择参数设置
     */
    void apply(@NonNull Activity activity, @Nullable Fragment fragment,
               SelectorOptions selectorOptions);

    /**
     * 获取多媒体选择器启动Intent
     * get MediaSelector Intent
     *
     * @param activity        activity调用方
     * @param selectorOptions 多媒体选择器选择参数设置
     * @return Intent
     */
    Intent getIntent(@NonNull Activity activity, SelectorOptions selectorOptions);

    /**
     * 获取多媒体选择器选择结果
     *
     * @param data 包含选择结果的Intent
     * @return 多媒体选择器选择结果
     */
    List<MediaEntity> getResult(Intent data);
}
