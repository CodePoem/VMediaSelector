package com.vdreamers.vmediaselector.core.scope;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图片类型限定
 * {@link ImageTypeConstants}
 * <p>
 * date 2019/09/18 15:15:55
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@IntDef({ImageTypeConstants.IMAGE_TYPE_PNG, ImageTypeConstants.IMAGE_TYPE_JPG,
        ImageTypeConstants.IMAGE_TYPE_GIF})
public @interface ImageType {

}
