package com.vdreamers.vmediaselector;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * 多媒体选择回调（文件形式 ）
 * <p>
 * date 2019-09-18 20:23:39
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface MediaSelectFilesCallback {

    /**
     * 多媒体选择成功回调
     *
     * @param uris       多媒体选择结果 Uri形式
     * @param files      多媒体选择结果 文件形式
     * @param resultCode 结果码
     * @param data       包含多媒体选择结果Intent
     */
    void onSuccess(int resultCode, Intent data, List<Uri> uris, List<File> files);

    /**
     * 多媒体选择失败回调
     *
     * @param mediaSelectError 多媒体选择异常
     */
    void onFailed(Throwable mediaSelectError);
}
