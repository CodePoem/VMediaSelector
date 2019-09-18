package com.vdreamers.vmediaselector.ui.impl;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.loader.IMediaCallback;
import com.vdreamers.vmediaselector.core.utils.MediaSelectorLogUtils;
import com.vdreamers.vmediaselector.ui.BaseMediaViewActivity;

import java.lang.ref.WeakReference;


/**
 * 展示原图Fragment
 * show raw image with the control of finger gesture.
 * <p>
 * date 2019-09-18 22:38:59
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaRawImageFragment extends MediaBaseFragment {
    /**
     * bundle传参-Image
     */
    private static final String BUNDLE_IMAGE = "ImageMediaEntity";
    private static final int MAX_SCALE = 15;
    private static final long MAX_IMAGE1 = 1024 * 1024L;
    private static final long MAX_IMAGE2 = 4 * MAX_IMAGE1;
    private static final int MAX_WIDTH = 1600;

    private PhotoView mImageView;
    private ProgressBar mProgress;
    private ImageMediaEntity mMedia;
    private PhotoViewAttacher mAttacher;

    public static MediaRawImageFragment newInstance(@NonNull ImageMediaEntity image) {
        MediaRawImageFragment fragment = new MediaRawImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMedia = getArguments().getParcelable(BUNDLE_IMAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_selector_ui_impl_fragment_raw_image, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = view.findViewById(R.id.loading);
        mImageView = view.findViewById(R.id.photo_view);
        mAttacher = new PhotoViewAttacher(mImageView);
    }

    @Override
    void setUserVisibleCompat(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Point point = getResizePointer(mMedia.getSize());
            ((BaseMediaViewActivity) getActivity()).loadRawImage(mImageView, mMedia,
                    point.x, point.y, new MediaCallback(this));
        }
    }

    /**
     * resize the image or not according to size.
     *
     * @param size the size of image
     */
    private Point getResizePointer(long size) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Point point = new Point(metrics.widthPixels, metrics.heightPixels);
        if (size >= MAX_IMAGE2) {
            point.x >>= 2;
            point.y >>= 2;
        } else if (size >= MAX_IMAGE1) {
            point.x >>= 1;
            point.y >>= 1;
        } else if (size > 0) {
            // avoid some images do not have a size.
            point.x = 0;
            point.y = 0;
        }
        return point;
    }

    private void dismissProgressDialog() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
        MediaViewActivity activity = getThisActivity();
        if (activity != null && activity.mProgressBar != null) {
            activity.mProgressBar.setVisibility(View.GONE);
        }
    }

    private MediaViewActivity getThisActivity() {
        Activity activity = getActivity();
        if (activity instanceof MediaViewActivity) {
            return (MediaViewActivity) activity;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAttacher != null) {
            mAttacher = null;
            mImageView = null;
        }
    }

    private static class MediaCallback implements IMediaCallback {
        private WeakReference<MediaRawImageFragment> mWr;

        MediaCallback(MediaRawImageFragment fragment) {
            mWr = new WeakReference<>(fragment);
        }

        @Override
        public void onSuccess() {
            if (mWr.get() == null || mWr.get().mImageView == null) {
                return;
            }
            mWr.get().dismissProgressDialog();
            Drawable drawable = mWr.get().mImageView.getDrawable();
            PhotoViewAttacher attacher = mWr.get().mAttacher;
            if (attacher != null) {
                if (drawable.getIntrinsicHeight() > (drawable.getIntrinsicWidth() << 2)) {
                    // handle the super height image.
                    int scale = drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
                    scale = Math.min(MAX_SCALE, scale);
                    attacher.setMaximumScale(scale);
                    attacher.setScale(scale, true);
                }
                attacher.update();
            }
            MediaViewActivity activity = mWr.get().getThisActivity();
            if (activity != null && activity.mGallery != null) {
                activity.mGallery.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFail(Throwable t) {
            if (mWr.get() == null) {
                return;
            }
            MediaSelectorLogUtils.d(t != null ? t.getMessage() : "load raw image error.");
            mWr.get().dismissProgressDialog();
            mWr.get().mImageView.setImageResource(R.drawable.v_selector_ui_impl_ic_broken_image);
            if (mWr.get().mAttacher != null) {
                mWr.get().mAttacher.update();
            }
        }
    }
}
