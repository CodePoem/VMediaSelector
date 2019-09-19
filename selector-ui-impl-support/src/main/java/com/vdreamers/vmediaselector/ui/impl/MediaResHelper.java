package com.vdreamers.vmediaselector.ui.impl;

import android.support.annotation.DrawableRes;

import com.vdreamers.vmediaselector.core.option.SelectorOptions;

/**
 * 资源帮助类
 * Help getting the resource in config.
 * <p>
 * date 2019-09-18 22:38:55
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaResHelper {

    @DrawableRes
    public static int getMediaPlaceHolderRes() {
        int result = SelectorOptions.getInstance().getMediaPlaceHolderRes();
        return result > 0 ? result : R.drawable.v_selector_ui_impl_ic_broken_image;
    }

    @DrawableRes
    public static int getAlbumPlaceHolderRes() {
        int result = SelectorOptions.getInstance().getAlbumPlaceHolderRes();
        return result > 0 ? result : R.drawable.v_selector_ui_impl_ic_broken_image;
    }

    @DrawableRes
    public static int getVideoDurationRes() {
        int result = SelectorOptions.getInstance().getVideoDurationRes();
        return result > 0 ? result : 0;
    }

    @DrawableRes
    public static int getMediaCheckedRes() {
        int result = SelectorOptions.getInstance().getMediaCheckedRes();
        return result > 0 ? result : R.drawable.v_selector_ui_impl_ic_checked;
    }

    @DrawableRes
    public static int getMediaUncheckedRes() {
        int result = SelectorOptions.getInstance().getMediaUnCheckedRes();
        return result > 0 ? result : R.drawable.v_selector_ui_impl_shape_unchecked;
    }

    @DrawableRes
    public static int getCameraRes() {
        int result = SelectorOptions.getInstance().getCameraRes();
        return result > 0 ? result : R.drawable.v_selector_ui_impl_ic_camera_white;
    }
}
