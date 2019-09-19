package com.vdreamers.vmediaselector.sample.custom;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.loader.IMediaCallback;
import com.vdreamers.vmediaselector.core.loader.IMediaLoader;

/**
 * 默认多媒体加载器-Glide
 * <p>
 * date 2019-09-18 21:05:44
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class DefaultGlideLoader implements IMediaLoader {

    @Override
    public void displayThumbnail(@NonNull ImageView img, @NonNull MediaEntity media, int width,
                                 int height) {
        if (media instanceof ImageMediaEntity) {
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.v_selector_ui_impl_ic_broken_image)
//                    .error(R.drawable.v_selector_ui_impl_ic_broken_image)
//                    .centerCrop();
//
//            Glide.with(img)
//                    .load(media.getUri())
//                    .transition(new DrawableTransitionOptions().crossFade())
//                    .apply(requestOptions)
//                    .into(img);
            Glide.with(img)
                    .load(media.getUri())
                    .placeholder(com.vdreamers.vmediaselector.R.drawable.v_selector_ui_impl_ic_broken_image)
                    .override(width, height)
                    .centerCrop()
                    .into(img);
        }
    }

    @Override
    public void displayRaw(@NonNull final ImageView img, @NonNull MediaEntity media, int width,
                           int height, final IMediaCallback callback) {
        if (media instanceof ImageMediaEntity) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(com.vdreamers.vmediaselector.R.drawable.v_selector_ui_impl_ic_broken_image)
                    .error(com.vdreamers.vmediaselector.R.drawable.v_selector_ui_impl_ic_broken_image);
            if (width > 0 && height > 0) {
                requestOptions.override(width, height);
            }
            Glide.with(img)
                    .load(media.getUri())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            if (callback != null) {
                                callback.onFail(e);
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            if (callback != null) {
                                img.setImageDrawable(resource);
                                callback.onSuccess();
                                return true;
                            }
                            return false;
                        }
                    })
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(requestOptions)
                    .into(img);
        }
    }
}
