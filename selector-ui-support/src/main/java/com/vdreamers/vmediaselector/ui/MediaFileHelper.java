package com.vdreamers.vmediaselector.ui;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.utils.MediaSelectorLogUtils;

import java.io.File;

/**
 * 文件辅助类
 * <p>
 * date 2019-09-18 22:01:00
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions", "unused"})
public class MediaFileHelper {
    /**
     * 默认子目录
     */
    public static final String DEFAULT_SUB_DIR = "/mediaselector/vmediaselector";

    public static String getSubDir() {
        // 先读取配置项子目录
        String subDir = SelectorOptions.getInstance().getSubDir();
        if (!TextUtils.isEmpty(subDir)) {
            return subDir;
        }
        return DEFAULT_SUB_DIR;
    }

    public static boolean createFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        final File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    @Nullable
    public static String getCacheDir(@NonNull Context context, String subDir) {
        if (context == null) {
            return null;
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            context = context.getApplicationContext();
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir == null) {
                MediaSelectorLogUtils.d("cache dir do not exist.");
                return null;
            }
            String dir = getSubDir();
            if (!TextUtils.isEmpty(subDir)) {
                dir = subDir;
            }
            String result = cacheDir.getAbsolutePath() + dir;
            MediaSelectorLogUtils.d("cache dir is: " + result);
            return result;
        }
        MediaSelectorLogUtils.d("external DCIM do not exist.");
        return null;
    }

    @Nullable
    public static String getExternalDCIM(String subDir) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (file == null) {
                return null;
            }
            String dir = getSubDir();
            if (!TextUtils.isEmpty(subDir)) {
                dir = subDir;
            }
            String result = file.getAbsolutePath() + dir;
            MediaSelectorLogUtils.d("external DCIM is: " + result);
            return result;
        }
        MediaSelectorLogUtils.d("external DCIM do not exist.");
        return null;
    }

    public static boolean isFileValid(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return isFileValid(file);
    }

    static boolean isFileValid(File file) {
        return file != null && file.exists() && file.isFile() && file.length() > 0 && file.canRead();
    }
}
