package com.vdreamers.vmediaselector.core.option;


import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 选择器预览模式限定注解
 * {@link SelectorViewModeConstants}
 * <p>
 * date 2019-09-18 20:25:03
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("WeakerAccess")
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@IntDef({SelectorViewModeConstants.VIEW_MODE_PREVIEW, SelectorViewModeConstants.VIEW_MODE_EDIT,
        SelectorViewModeConstants.VIEW_MODE_PREVIEW_EDIT})
public @interface SelectorViewMode {
}
