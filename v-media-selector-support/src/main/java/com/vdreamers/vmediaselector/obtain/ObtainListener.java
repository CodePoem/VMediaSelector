package com.vdreamers.vmediaselector.obtain;

import java.util.List;

/**
 * 获取监听器
 * <p>
 * date 2019/09/19 14:12:43
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface ObtainListener {
    /**
     * 获取开始
     */
    void onStart();

    /**
     * 获取成功回调
     *
     * @param obtainFilePathList 获取的文件路径列表
     */
    void onSuccess(List<String> obtainFilePathList);

    /**
     * 获取失败回调
     *
     * @param throwable 获取失败异常
     */
    void onFailed(Throwable throwable);
}
