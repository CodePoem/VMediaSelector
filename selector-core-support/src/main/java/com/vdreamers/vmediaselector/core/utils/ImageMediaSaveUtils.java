package com.vdreamers.vmediaselector.core.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;

import java.io.File;

/**
 * 图片媒体保存工具类
 * <p>
 * date 2019/09/19 15:19:49
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class ImageMediaSaveUtils {

    /**
     * 保存图片到媒体存储
     * save image to MediaStore.
     * @param context 调用方上下文
     * @param cr 内容解析器
     * @param file 保存的文件
     */
    public static ImageMediaEntity saveMediaStore(final Context context, final ContentResolver cr, final File file) {
        if (context == null) {
            return null;
        }
        final ImageMediaEntity cameraMedia = new ImageMediaEntity(file);

        LoadExecutorUtils.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                if (cr != null && !TextUtils.isEmpty(cameraMedia.getId())) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, cameraMedia.getId());
                    values.put(MediaStore.Images.Media.MIME_TYPE, cameraMedia.getMimeType());
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }
        });
        return cameraMedia;
    }
}
