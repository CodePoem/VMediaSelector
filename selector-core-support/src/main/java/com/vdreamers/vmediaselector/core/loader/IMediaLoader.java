package com.vdreamers.vmediaselector.core.loader;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;

/**
 * 多媒体加载接口定义
 * <p>
 * date 2019-09-18 20:24:31
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface IMediaLoader {
    /**
     * 展示缩略图
     * display thumbnail images for a ImageView.
     *
     * @param img    the display ImageView. Through ImageView.getTag(R.string.app_name) to get
     *               the absolute path of the exact path to display.
     * @param media  the image media to display, may be out of date when fast scrolling.
     * @param width  the resize with for the image.
     * @param height the resize height for the image.
     */
    void displayThumbnail(@NonNull ImageView img, @NonNull MediaEntity media, int width,
                          int height);

    /**
     * 展示原图
     * display raw images for a ImageView, need more work to do.
     *
     * @param img      the display ImageView.Through ImageView.getTag(R.string.app_name) to get
     *                 the absolute path of the exact path to display.
     * @param media    the image media to display, may be out of date when fast scrolling.
     * @param width    the expected width, 0 means the raw width.
     * @param height   the expected height, 0 means the raw height.
     */
    void displayRaw(@NonNull ImageView img, @NonNull MediaEntity media, int width, int height);
}
