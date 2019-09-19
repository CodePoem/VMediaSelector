package com.vdreamers.vmediaselector.core.loader;

/**
 * 多媒体回调
 * <p>
 * date 2019-09-18 20:24:25
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface IMediaCallback {

    /**
     * 成功处理任务
     * Successfully handle a task;
     */
    void onSuccess();

    /**
     * 处理任务过程发生错误
     * Error happened when running a task;
     *
     * @param t 错误
     */
    void onFail(Throwable t);
}
