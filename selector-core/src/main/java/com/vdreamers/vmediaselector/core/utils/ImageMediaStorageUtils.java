package com.vdreamers.vmediaselector.core.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;

import java.io.File;

/**
 * 图片媒体保存工具类
 * <p>
 * date 2019/09/19 15:19:49
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class ImageMediaStorageUtils {

    /**
     * 保存图片到媒体存储
     * save image to MediaStore.
     *
     * @param context 调用方上下文
     * @param cr      内容解析器
     * @param file    保存的文件
     */
    public static ImageMediaEntity saveMediaStore(final Context context, final ContentResolver cr
            , final File file, Uri uri) {
        if (context == null) {
            return null;
        }
        final ImageMediaEntity cameraMedia = new ImageMediaEntity(file, uri);

        LoadExecutorUtils.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                if (cr != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, cameraMedia.getTitle());
                    values.put(MediaStore.Images.Media.MIME_TYPE, cameraMedia.getMimeType());
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }
        });
        return cameraMedia;
    }

    /**
     * 从媒体存储删除图片
     *
     * @param cr               内容解析器
     * @param imageMediaEntity 删除图片
     * @return
     */
    public static ImageMediaEntity deleteMediaStore(final ContentResolver cr,
                                                    final ImageMediaEntity imageMediaEntity) {
        LoadExecutorUtils.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                if (cr != null) {
                    String where =
                            MediaStore.Images.Media._ID + "='" + imageMediaEntity.getId() + "'";
                    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);
                }
            }
        });
        return imageMediaEntity;
    }
}
