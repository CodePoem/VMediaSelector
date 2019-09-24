package com.vdreamers.vmediaselector.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.vdreamers.vmediaselector.core.option.SelectorOptions;

import java.io.File;

/**
 * FileProvider工具类
 * <p>
 * date 2019/09/24 15:40:49
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class FileProviderUtils {
    /**
     * 获取文件Uri
     *
     * @param context 调用方上下文
     * @param file    文件
     * @return Uri
     */
    public static Uri getFileUri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = SelectorOptions.getInstance().getAuthorities();
            // 如果使用方未设置fileProvider 提供默认的fileProvider
            if (authorities == null) {
                authorities = context.getPackageName() + ".vmediaselector.file.provider";
            }
            return FileProvider.getUriForFile(context, authorities, file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
