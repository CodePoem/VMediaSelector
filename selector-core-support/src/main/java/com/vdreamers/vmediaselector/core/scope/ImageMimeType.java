package com.vdreamers.vmediaselector.core.scope;

import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图片媒体类型限定
 * {@link ImageMimeTypeConstants}
 * <p>
 * date 2019/09/18 15:15:55
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@StringDef({ImageMimeTypeConstants.IMAGE_GIF, ImageMimeTypeConstants.IMAGE_PNG,
        ImageMimeTypeConstants.IMAGE_JPG, ImageMimeTypeConstants.IMAGE_JPEG})
public @interface ImageMimeType {
}
