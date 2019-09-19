package com.vdreamers.vmediaselector.ui.impl.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
    private View mVideoLayout;
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
        mVideoLayout = view.findViewById(R.id.video_layout);
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
        if (media instanceof ImageMediaEntity) {
            mVideoLayout.setVisibility(GONE);
            setCover(((ImageMediaEntity) media));
        } else if (media instanceof VideoMediaEntity) {
            mVideoLayout.setVisibility(VISIBLE);
            VideoMediaEntity videoMedia = (VideoMediaEntity) media;
            TextView durationTxt = ((TextView) mVideoLayout.findViewById(R.id.video_duration_txt));
            durationTxt.setText(videoMedia.getDuration());
            durationTxt.setCompoundDrawablesWithIntrinsicBounds(SelectorOptions.getInstance().getVideoDurationRes(), 0, 0, 0);
            ((TextView) mVideoLayout.findViewById(R.id.video_size_txt)).setText(videoMedia.getSizeByUnit());
            setCover(videoMedia);
        }
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
