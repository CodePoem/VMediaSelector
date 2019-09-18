package com.vdreamers.vmediaselector.core.option;


import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 选择器模式限定注解
 * {@link SelectorModeConstants}
 * <p>
 * date 2019-09-18 20:24:41
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("WeakerAccess")
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@IntDef({SelectorModeConstants.MODE_IMAGE, SelectorModeConstants.MODE_VIDEO})
public @interface SelectorMode {
}
