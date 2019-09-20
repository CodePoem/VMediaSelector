package com.vdreamers.vmediaselector.core.selector;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.vdreamers.vmediaselector.core.callback.MediaSelectCallback;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 多媒体选择器
 * <p>
 * date 2019-09-18 20:25:44
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public class MediaSelector {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    /**
     * 多媒体选择器实现类
     */
    private IMediaSelector mIMediaSelector;
    /**
     * 多媒体选项参数实现类
     */
    private SelectorOptions mSelectorOptions;
    /**
     * 多媒体选择器意图
     */
    private Intent mIntent;
    /**
     * 分页单页数量限制
     */
    public static final int PAGE_LIMIT = 1000;
    /**
     * 额外参数-选中的多媒体列表
     */
    public static final String EXTRA_SELECTED_MEDIA = "extra_selected_media";
    /**
     * 额外参数-选择的多媒体结果列表
     */
    public static final String EXTRA_RESULT = "extra_result";
    /**
     * 额外参数-开始位置
     */
    public static final String EXTRA_START_POS = "extra_start_pos";
    /**
     * 额外参数-相册Id
     */
    public static final String EXTRA_ALBUM_ID = "extra_album_id";

    private MediaSelector(Activity activity) {
        this(activity, null);
    }

    private MediaSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private MediaSelector(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * 创建一个多媒体选择器
     *
     * @param activity activity调用方
     * @return 一个多媒体选择器
     */
    public static MediaSelector of(Activity activity) {
        return new MediaSelector(activity);
    }

    /**
     * 创建一个多媒体选择器
     *
     * @param fragment fragment调用方
     * @return 一个多媒体选择器
     */
    public static MediaSelector of(Fragment fragment) {
        return new MediaSelector(fragment);
    }


    public List<MediaEntity> getResult(Intent data) {
        if (mIMediaSelector == null) {
            return new ArrayList<>();
        }
        return mIMediaSelector.getResult(data);
    }

    /**
     * 设置多媒体选择器
     *
     * @param iMediaSelector 多媒体选择器数实现类
     * @return 当前多媒体选择器
     */
    public MediaSelector withSelector(@NonNull IMediaSelector iMediaSelector) {
        mIMediaSelector = iMediaSelector;
        if (mIMediaSelector != null) {
            mIMediaSelector.init();
        }
        return this;
    }

    public IMediaSelector getIMediaSelector() {
        return mIMediaSelector;
    }

    /**
     * 设置多媒体选项参数
     *
     * @param selectorOptions 多媒体选项参数
     * @return 当前多媒体选择器
     */
    public MediaSelector withOptions(@NonNull SelectorOptions selectorOptions) {
        mSelectorOptions = selectorOptions;
        if (mSelectorOptions != null && mIMediaSelector != null) {
            Activity activity = getActivity();
            if (activity == null) {
                return this;
            }
            mIMediaSelector.apply(activity, getFragment(), selectorOptions);
        }
        return this;
    }

    public SelectorOptions getISelectorOptions() {
        return mSelectorOptions;
    }

    /**
     * 设置多媒体选择器意图
     *
     * @param intent 多媒体选择器意图
     * @return 当前多媒体选择器
     */
    public MediaSelector withIntent(Intent intent) {
        mIntent = intent;
        return this;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void start(int requestCode, MediaSelectCallback mediaSelectCallback) {
        start(requestCode, null, mediaSelectCallback);
    }

    public void start(int requestCode, ArrayList<? extends MediaEntity> medias,
                      MediaSelectCallback mediaSelectCallback) {
        start(requestCode, medias, -1, null, mediaSelectCallback);
    }

    public void start(int requestCode, ArrayList<? extends MediaEntity> medias, int pos,
                      String albumId, MediaSelectCallback mediaSelectCallback) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Fragment fragment = getFragment();
        if (mIntent == null && mIMediaSelector != null) {
            mIntent = mIMediaSelector.getIntent(activity, mSelectorOptions);
        }
        if (mIntent == null) {
            return;
        }
        if (medias != null && !medias.isEmpty()) {
            mIntent.putExtra(EXTRA_SELECTED_MEDIA, medias);
        }
        if (pos >= 0) {
            mIntent.putExtra(EXTRA_START_POS, pos);
        }
        if (albumId != null) {
            mIntent.putExtra(EXTRA_ALBUM_ID, albumId);
        }
        if (fragment != null) {
            MediaSelectOnResult.of(fragment)
                    .start(mIntent, requestCode, mediaSelectCallback, this);
        } else {
            MediaSelectOnResult.of(activity)
                    .start(mIntent, requestCode, mediaSelectCallback, this);
        }
    }

    @Nullable
    Activity getActivity() {
        return mActivity.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }
}
