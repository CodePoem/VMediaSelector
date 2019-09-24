package com.vdreamers.vmediaselector.ui.impl.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.entity.VideoMediaEntity;
import com.vdreamers.vmediaselector.core.loader.MediaLoader;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.ui.impl.MediaResHelper;
import com.vdreamers.vmediaselector.ui.impl.R;
import com.vdreamers.vmediaselector.ui.impl.WindowManagerHelper;

/**
 * 多媒体列表项布局
 * A media layout for {@link RecyclerView} item, including image and
 * video
 * <p>
 * date 2019-09-18 21:58:13
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaItemLayout extends FrameLayout {
    private static final int BIG_IMG_SIZE = 5 * 1024 * 1024;

    private ImageView mCheckImg;
    private View mBottomInfoLayout;
    private View mFontLayout;
    private ImageView mCoverImg;
    private ScreenType mScreenType;

    private enum ScreenType {
        SMALL(100), NORMAL(180), LARGE(320);
        int value;

        ScreenType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public MediaItemLayout(Context context) {
        this(context, null, 0);
    }

    public MediaItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view =
                LayoutInflater.from(context).inflate(R.layout.v_selector_ui_impl_layout_media_item, this,
                        true);
        mCoverImg = (ImageView) view.findViewById(R.id.media_item);
        mCheckImg = (ImageView) view.findViewById(R.id.media_item_check);
        mBottomInfoLayout = view.findViewById(R.id.layout_bottom_info);
        mFontLayout = view.findViewById(R.id.media_font_layout);
        mScreenType = getScreenType(context);
        setImageRect(context);
    }

    private void setImageRect(Context context) {
        int screenHeight = WindowManagerHelper.getScreenHeight(context);
        int screenWidth = WindowManagerHelper.getScreenWidth(context);
        int width = 100;
        if (screenHeight != 0 && screenWidth != 0) {
            width = (screenWidth - getResources().getDimensionPixelOffset(R.dimen.v_selector_ui_impl_media_margin) * 4) / 3;
        }
        mCoverImg.getLayoutParams().width = width;
        mCoverImg.getLayoutParams().height = width;
        mFontLayout.getLayoutParams().width = width;
        mFontLayout.getLayoutParams().height = width;
    }

    private ScreenType getScreenType(Context context) {
        int type =
                context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        ScreenType result;
        switch (type) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                result = ScreenType.SMALL;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                result = ScreenType.NORMAL;
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                result = ScreenType.LARGE;
                break;
            default:
                result = ScreenType.NORMAL;
                break;
        }
        return result;
    }

    public void setImageRes(@DrawableRes int imageRes) {
        if (mCoverImg != null) {
            mCoverImg.setImageResource(imageRes);
        }
    }

    public void setMedia(MediaEntity media) {
        TextView tvMediaInfo = mBottomInfoLayout.findViewById(R.id.tv_media_info);
        ((TextView) mBottomInfoLayout.findViewById(R.id.tv_media_size)).setText(media.getSizeByUnit());
        if (media instanceof ImageMediaEntity) {
            ImageMediaEntity imageMediaEntity = (ImageMediaEntity) media;
            tvMediaInfo.setText(getImageType(imageMediaEntity));
            setCover(media);
        } else if (media instanceof VideoMediaEntity) {
            VideoMediaEntity videoMedia = (VideoMediaEntity) media;
            tvMediaInfo.setText(videoMedia.getDuration());
            tvMediaInfo.setCompoundDrawablesWithIntrinsicBounds(SelectorOptions.getInstance().getVideoDurationRes(), 0, 0, 0);
            setCover(videoMedia);
        }
    }

    private CharSequence getImageType(ImageMediaEntity imageMediaEntity) {
        if (imageMediaEntity.isPng()) {
            return getContext().getString(R.string.v_selector_ui_impl_png);
        } else if (imageMediaEntity.isJpg()) {
            return getContext().getString(R.string.v_selector_ui_impl_jpg);
        } else if (imageMediaEntity.isGif()) {
            return getContext().getString(R.string.v_selector_ui_impl_gif);
        }
        return "";
    }

    private void setCover(@NonNull MediaEntity imageMedia) {
        if (mCoverImg == null || imageMedia == null) {
            return;
        }
        mCoverImg.setTag(R.string.v_selector_ui_impl_app_name, imageMedia);
        MediaLoader.getInstance().displayThumbnail(mCoverImg, imageMedia, mScreenType.getValue(),
                mScreenType.getValue());
    }

    @SuppressWarnings("deprecation")
    public void setChecked(boolean isChecked) {
        if (isChecked) {
            mFontLayout.setVisibility(View.VISIBLE);
            mCheckImg.setImageDrawable(getResources().getDrawable(MediaResHelper.getMediaCheckedRes()));
        } else {
            mFontLayout.setVisibility(View.GONE);
            mCheckImg.setImageDrawable(getResources().getDrawable(MediaResHelper.getMediaUncheckedRes()));
        }
    }

}
