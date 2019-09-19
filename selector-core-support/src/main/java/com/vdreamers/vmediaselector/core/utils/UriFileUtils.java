package com.vdreamers.vmediaselector.core.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uri相关工具类
 * <p>
 * date 2019-09-18 20:25:56
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public class UriFileUtils {

    /**
     * 缓冲大小
     */
    private static final int BUFFER_SIZE = 8 * 1024;

    /**
     * 从Uri获取文件
     *
     * @param context 调用方上下文
     * @param uri     Uri
     * @return 文件
     */
    @Nullable
    public static File getFileFromUri(@NonNull Context context, @NonNull Uri uri) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            return null;
        }
        switch (scheme) {
            case "content":
                String fileName = getFileDisPlayName(context, uri);
                File file = getFileFromInputStreamUri(context, uri, fileName);
                if (file == null) {
                    file = getFileFromContentUri(context, uri);
                }
                return file;
            case "file":
                String path = uri.getPath();
                if (path == null) {
                    return null;
                }
                return new File(path);
            default:
                return null;
        }
    }

    /**
     * 从content scheme Uri（content://）获取文件
     *
     * @param context    调用方上下文
     * @param contentUri content scheme Uri
     * @return 文件
     */
    @Nullable
    public static File getFileFromContentUri(@NonNull Context context, @NonNull Uri contentUri) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        if (contentUri == null) {
            throw new IllegalArgumentException("contentUri cannot be null");
        }
        File file = null;
        try {
            String filePath;
            String[] queryColumn = {MediaStore.MediaColumns.DATA};
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(contentUri, queryColumn, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndex(queryColumn[0]));
                cursor.close();
                if (!TextUtils.isEmpty(filePath)) {
                    file = new File(filePath);
                }
                if (file.exists() && file.length() > 0 && !TextUtils.isEmpty(filePath)) {
                    file = new File(filePath);
                }
            }
        } catch (Exception e) {
            MediaSelectorLogUtils.e(e);
        }
        return file;
    }

    /**
     * 通过流从Uri获取文件（拷贝文件一份到自己APP目录下）
     *
     * @param context  调用方上下文
     * @param uri      Uri
     * @param fileName 保存文件名
     * @return 文件
     */
    @Nullable
    public static File getFileFromInputStreamUri(@NonNull Context context, @NonNull Uri uri,
                                                 String fileName) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }

        InputStream inputStream = null;
        File file = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                file = createCacheFileFromStream(context, inputStream, fileName);
            } catch (Exception e) {
                MediaSelectorLogUtils.e(e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    MediaSelectorLogUtils.e(e);
                }
            }
        }
        return file;
    }

    /**
     * 获取文件展示名
     *
     * @param context 调用方上下文
     * @param uri     Uri
     * @return 文件名展示名
     */
    public static String getFileDisPlayName(@NonNull Context context, @NonNull Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] queryColumn = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(uri, queryColumn, null,
                null, null);
        String fileName = "";
        if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndex(queryColumn[0]));
            cursor.close();
        }
        return fileName;
    }

    /**
     * 从流获取缓存文件
     *
     * @param context     调用方上下文
     * @param inputStream 输入流
     * @param fileName    文件名
     * @return 缓存文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File createCacheFileFromStream(@NonNull Context context, InputStream inputStream,
                                                  String fileName)
            throws IOException {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[BUFFER_SIZE];
            // 拷贝文件到外部存储缓存路径
            targetFile = new File(getExternalPrivateCacheDir(context), fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return targetFile;
    }

    /**
     * 获取主外部存储私有目录缓存目录
     *
     * @param context context 调用方上下文
     * @return file 主外部存储私有目录缓存目录
     */
    public static File getExternalPrivateCacheDir(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        if (!isSDCardEnable()) {
            return null;
        }
        return context.getExternalCacheDir();
    }


    /**
     * 判断SDCard是否可用
     *
     * @return SDCard是否可用 true：可用 false：不可用
     */
    public static boolean isSDCardEnable() {
        return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable());
    }
}
