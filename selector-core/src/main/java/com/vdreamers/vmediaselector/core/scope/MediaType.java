package com.vdreamers.vmediaselector.core.scope;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多媒体类型限定
 * {@link MediaTypeConstants}
 * <p>
 * date 2019/09/18 15:16:13
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@IntDef({MediaTypeConstants.MEDIA_TYPE_IMAGE, MediaTypeConstants.MEDIA_TYPE_VIDEO})
public @interface MediaType {
}
