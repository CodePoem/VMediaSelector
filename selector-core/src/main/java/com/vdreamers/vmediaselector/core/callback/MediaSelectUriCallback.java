package com.vdreamers.vmediaselector.core.callback;

import android.content.Intent;
import android.net.Uri;

import java.util.List;

/**
 * 多媒体选择回调（Uri形式）
 * <p>
 * date 2019-09-18 20:23:46
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface MediaSelectUriCallback {

    /**
     * 多媒体选择成功回调
     *
     * @param resultCode 结果码
     * @param data       包含多媒体选择结果Intent
     * @param uris       多媒体选择结果Uri形式
     */
    void onMediaSelectSuccess(int resultCode, Intent data, List<Uri> uris);

    /**
     * 多媒体选择异常回调
     *
     * @param mediaSelectError 多媒体选择异常
     */
    void onMediaSelectError(Throwable mediaSelectError);
}