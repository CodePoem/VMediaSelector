package com.vdreamers.vmediaselector.core.loader;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;

/**
 * 多媒体加载器
 * <p>
 * date 2019-09-18 20:24:36
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("unused")
public class MediaLoader {
    /**
     * 加载器
     */
    private IMediaLoader mLoader;

    private MediaLoader() {
    }

    private static class SingletonHolder {
        private static final MediaLoader INSTANCE = new MediaLoader();
    }

    public static MediaLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(@NonNull IMediaLoader loader) {
        this.mLoader = loader;
    }

    public void displayThumbnail(@NonNull ImageView img, @NonNull MediaEntity media, int width,
                                 int height) {
        if (ensureLoader()) {
            throw new IllegalStateException("init method should be called first");
        }
        mLoader.displayThumbnail(img, media, width, height);
    }

    public void displayRaw(@NonNull ImageView img, @NonNull MediaEntity media, int width,
                           int height,
                           IMediaCallback callback) {
        if (ensureLoader()) {
            throw new IllegalStateException("init method should be called first");
        }
        mLoader.displayRaw(img, media, width, height, callback);
    }

    public IMediaLoader getLoader() {
        return mLoader;
    }

    private boolean ensureLoader() {
        return mLoader == null;
    }
}
