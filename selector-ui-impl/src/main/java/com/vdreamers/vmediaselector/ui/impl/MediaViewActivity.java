package com.vdreamers.vmediaselector.ui.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.vdreamers.vmediaselector.core.contract.SelectorContract;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.impl.presenter.SelectorPresenter;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;
import com.vdreamers.vmediaselector.ui.BaseMediaViewActivity;
import com.vdreamers.vmediaselector.ui.impl.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 默认UI 展示原图Activity
 * An Activity to show raw image by holding {@link MediaViewFragment}.
 * <p>
 * date 2019-09-18 22:29:48
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaViewActivity extends BaseMediaViewActivity {
    public static final String EXTRA_TYPE_BACK = "extra_type_back";

    /**
     * 预览ViewPage
     */
    HackyViewPager mGallery;
    /**
     * 加载进度条
     */
    ProgressBar mProgressBar;
    /**
     * 是否需要编辑
     */
    private boolean mNeedEdit;
    /**
     * 是否需要从数据库加载
     */
    private boolean mNeedLoading;
    /**
     * 加载是否结束
     */
    private boolean mFinishLoading;
    /**
     * 是否需要显示总计
     */
    private boolean mNeedAllCount = true;
    /**
     * 当前页
     */
    private int mCurrentPage;
    /**
     * 总计
     */
    private int mTotalCount;
    /**
     * 开始位置
     */
    private int mStartPos;
    /**
     * 当前位置
     */
    private int mPos;
    /**
     * 最大选择数
     */
    private int mMaxCount;

    private String mAlbumId;
    private Toolbar mToolbar;
    private ImagesAdapter mAdapter;
    private ImageMediaEntity mCurrentImageItem;
    private Button mOkBtn;
    private ArrayList<MediaEntity> mImages;
    private ArrayList<MediaEntity> mSelectedImages;
    private MenuItem mSelectedMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_selector_ui_impl_activity_media_view);
        createToolbar();
        initData();
        initView();
        startLoading();
    }

    @Override
    public SelectorContract.Presenter createPresenter(SelectorContract.View view) {
        return new SelectorPresenter(view);
    }

    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.nav_top_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initData() {
        mSelectedImages = getSelectedMedias();
        mAlbumId = getAlbumId();
        mStartPos = getStartPos();
        mNeedLoading = SelectorOptions.getInstance().isNeedLoading();
        mNeedEdit = SelectorOptions.getInstance().isNeedEdit();
        mMaxCount = getMaxCount();
        mImages = new ArrayList<>();
        if (!mNeedLoading && mSelectedImages != null) {
            mImages.addAll(mSelectedImages);
        }
    }

    private void initView() {
        mAdapter = new ImagesAdapter(getSupportFragmentManager());
        mOkBtn = (Button) findViewById(R.id.image_items_ok);
        mGallery = (HackyViewPager) findViewById(R.id.pager);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mGallery.setAdapter(mAdapter);
        mGallery.addOnPageChangeListener(new OnPagerChangeListener());
        if (!mNeedEdit) {
            View chooseLayout = findViewById(R.id.item_choose_layout);
            chooseLayout.setVisibility(View.GONE);
        } else {
            setOkTextNumber();
            mOkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishByBackPressed(false);
                }
            });
        }
    }

    private void setOkTextNumber() {
        if (mNeedEdit) {
            int selectedSize = mSelectedImages.size();
            int size = Math.max(mSelectedImages.size(), mMaxCount);
            mOkBtn.setText(getString(R.string.v_selector_ui_impl_image_preview_ok_fmt,
                    String.valueOf(selectedSize)
                    , String.valueOf(size)));
            mOkBtn.setEnabled(selectedSize > 0);
        }
    }

    private void finishByBackPressed(boolean value) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(MediaSelector.EXTRA_SELECTED_MEDIA, mSelectedImages);
        intent.putExtra(EXTRA_TYPE_BACK, value);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mNeedEdit) {
            getMenuInflater().inflate(R.menu.v_selector_ui_impl_activity_image_viewer, menu);
            mSelectedMenuItem = menu.findItem(R.id.menu_image_item_selected);
            if (mCurrentImageItem != null) {
                setMenuIcon(mCurrentImageItem.isSelected());
            } else {
                setMenuIcon(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_image_item_selected) {
            if (mCurrentImageItem == null) {
                return false;
            }
            if (mSelectedImages.size() >= mMaxCount && !mCurrentImageItem.isSelected()) {
                String warning = getString(R.string.v_selector_ui_impl_too_many_picture_fmt,
                        mMaxCount);
                Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (mCurrentImageItem.isSelected()) {
                cancelImage();
            } else {
                if (!mSelectedImages.contains(mCurrentImageItem)) {
                    if (mCurrentImageItem.isGifOverSize()) {
                        Toast.makeText(getApplicationContext(),
                                R.string.v_selector_ui_impl_gif_too_big, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    mCurrentImageItem.setSelected(true);
                    mSelectedImages.add(mCurrentImageItem);
                }
            }
            setOkTextNumber();
            setMenuIcon(mCurrentImageItem.isSelected());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelImage() {
        if (mSelectedImages.contains(mCurrentImageItem)) {
            mSelectedImages.remove(mCurrentImageItem);
        }
        mCurrentImageItem.setSelected(false);
    }


    private void setMenuIcon(boolean isSelected) {
        if (mNeedEdit) {
            mSelectedMenuItem.setIcon(isSelected ? MediaResHelper.getMediaCheckedRes() :
                    MediaResHelper.getMediaUncheckedRes());
        }
    }

    @Override
    public void startLoading() {
        if (!mNeedLoading) {
            mCurrentImageItem = (ImageMediaEntity) mSelectedImages.get(mStartPos);
            mToolbar.setTitle(getString(R.string.v_selector_ui_impl_image_preview_title_fmt,
                    String.valueOf(mStartPos + 1)
                    , String.valueOf(mSelectedImages.size())));
            mProgressBar.setVisibility(View.GONE);
            mGallery.setVisibility(View.VISIBLE);
            mAdapter.setMedias(mImages);
            if (mStartPos > 0 && mStartPos < mSelectedImages.size()) {
                mGallery.setCurrentItem(mStartPos, false);
            }
        } else {
            // 加载当前页
            loadMedia(mAlbumId, mStartPos, mCurrentPage);
            mAdapter.setMedias(mImages);
        }
    }

    private void loadMedia(String albumId, int startPos, int page) {
        this.mPos = startPos;
        loadMedias(page, albumId);
    }

    @Override
    public void showMedia(@Nullable List<MediaEntity> medias, int totalCount) {
        if (medias == null || totalCount <= 0) {
            return;
        }
        mImages.addAll(medias);
        mAdapter.notifyDataSetChanged();
        checkSelectedMedia(mImages, mSelectedImages);
        setupGallery();

        if (mToolbar != null && mNeedAllCount) {
            mToolbar.setTitle(getString(R.string.v_selector_ui_impl_image_preview_title_fmt,
                    String.valueOf(++mPos), String.valueOf(totalCount)));
            mNeedAllCount = false;
        }
        loadOtherPagesInAlbum(totalCount);
    }

    private void setupGallery() {
        int startPos = mStartPos;
        if (mGallery == null || startPos < 0) {
            return;
        }
        if (startPos < mImages.size() && !mFinishLoading) {
            mGallery.setCurrentItem(mStartPos, false);
            mCurrentImageItem = (ImageMediaEntity) mImages.get(startPos);
            mProgressBar.setVisibility(View.GONE);
            mGallery.setVisibility(View.VISIBLE);
            mFinishLoading = true;
            invalidateOptionsMenu();
        } else if (startPos >= mImages.size()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mGallery.setVisibility(View.GONE);
        }
    }

    private void loadOtherPagesInAlbum(int totalCount) {
        mTotalCount = totalCount;
        if (mCurrentPage <= (mTotalCount / MediaSelector.PAGE_LIMIT)) {
            mCurrentPage++;
            loadMedia(mAlbumId, mStartPos, mCurrentPage);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MediaSelector.EXTRA_ALBUM_ID, mAlbumId);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        finishByBackPressed(true);
    }

    private class ImagesAdapter extends FragmentStatePagerAdapter {
        private ArrayList<MediaEntity> mMedias;

        ImagesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return MediaPreViewFragment.newInstance((ImageMediaEntity) mMedias.get(i));
        }

        @Override
        public int getCount() {
            return mMedias == null ? 0 : mMedias.size();
        }

        public void setMedias(ArrayList<MediaEntity> medias) {
            this.mMedias = medias;
            notifyDataSetChanged();
        }
    }

    private class OnPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            if (mToolbar != null && position < mImages.size()) {
                mToolbar.setTitle(getString(R.string.v_selector_ui_impl_image_preview_title_fmt,
                        String.valueOf(position + 1)
                        , mNeedLoading ? String.valueOf(mTotalCount) :
                                String.valueOf(mImages.size())));
                mCurrentImageItem = (ImageMediaEntity) mImages.get(position);
                invalidateOptionsMenu();
            }
        }
    }
}
