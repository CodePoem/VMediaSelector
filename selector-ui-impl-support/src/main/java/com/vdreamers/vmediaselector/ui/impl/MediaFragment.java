package com.vdreamers.vmediaselector.ui.impl;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vdreamers.vmediaselector.core.callback.MediaSelectCallback;
import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.entity.VideoMediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.option.SelectorViewModeConstants;
import com.vdreamers.vmediaselector.core.scope.MediaTypeConstants;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;
import com.vdreamers.vmediaselector.core.utils.MediaSelectorLogUtils;
import com.vdreamers.vmediaselector.ui.BaseMediaViewFragment;
import com.vdreamers.vmediaselector.ui.MediaFileHelper;
import com.vdreamers.vmediaselector.ui.impl.adapter.AlbumAdapter;
import com.vdreamers.vmediaselector.ui.impl.adapter.MediaAdapter;
import com.vdreamers.vmediaselector.ui.impl.view.HackyGridLayoutManager;
import com.vdreamers.vmediaselector.ui.impl.view.MediaItemLayout;
import com.vdreamers.vmediaselector.ui.impl.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 视图层实现 默认UI Fragment
 * A full implement for {@link com.vdreamers.vmediaselector.core.contract.SelectorContract.View}
 * supporting all the mode
 * use this to pick the picture.
 * <p>
 * date 2019-09-18 21:40:50
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaFragment extends BaseMediaViewFragment implements View.OnClickListener {
    public static final String TAG = MediaFragment.class.getSimpleName();
    private static final int IMAGE_PREVIEW_REQUEST_CODE = 9086;
    /**
     * 表格布局列数
     */
    private static final int GRID_COUNT = 3;
    /**
     * 预览标志
     */
    private boolean mIsPreview;
    /**
     * 相机标志
     */
    private boolean mIsCamera;
    /**
     * 预览按钮
     */
    private Button mPreBtn;
    /**
     * 确定按钮
     */
    private Button mOkBtn;
    /**
     * 多媒体列表RecyclerView
     */
    private RecyclerView mRecycleView;
    /**
     * 多媒体适配器
     */
    private MediaAdapter mMediaAdapter;
    /**
     * 相册适配器
     */
    private AlbumAdapter mAlbumWindowAdapter;
    /**
     * 加载处理对话框
     */
    private ProgressDialog mDialog;
    /**
     * 空视图展示文本
     */
    private TextView mEmptyTxt;
    /**
     * 标题展示文本
     */
    private TextView mTitleTxt;
    /**
     * 相册窗口PopWindow
     */
    private PopupWindow mAlbumPopWindow;
    /**
     * 加载进度条
     */
    private ProgressBar mLoadingView;

    private int mMaxCount;

    public static MediaFragment newInstance() {
        return new MediaFragment();
    }

    @Override
    public void onCreateWithSelectedMedias(Bundle savedInstanceState,
                                           @Nullable List<MediaEntity> selectedMedias) {
        mAlbumWindowAdapter = new AlbumAdapter(getContext());
        mMediaAdapter = new MediaAdapter(getContext());
        mMediaAdapter.setSelectedMedias(selectedMedias);
        mMaxCount = getMaxCount();
    }

    @Override
    public void startLoading() {
        loadMedias();
        loadAlbum();
    }

    @Override
    public void onRequestPermissionError(String[] permissions, Exception e) {
        if (permissions.length > 0) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), R.string.v_selector_ui_impl_storage_permission_deny,
                        Toast.LENGTH_SHORT).show();
                showEmptyData();
            } else if (permissions[0].equals(Manifest.permission.CAMERA)) {
                Toast.makeText(getContext(), R.string.v_selector_ui_impl_camera_permission_deny,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionSuc(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
        if (permissions[0].equals(STORAGE_PERMISSIONS[0])) {
            startLoading();
        } else if (permissions[0].equals(CAMERA_PERMISSIONS[0])) {
            startCamera(getActivity(), this, MediaFileHelper.getSubDir());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_selector_ui_impl_fragmant_media_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View view) {
        mEmptyTxt = (TextView) view.findViewById(R.id.empty_txt);
        mRecycleView = (RecyclerView) view.findViewById(R.id.media_recycleview);
        mRecycleView.setHasFixedSize(true);
        mLoadingView = (ProgressBar) view.findViewById(R.id.loading);
        initRecycleView();

        boolean isMultiImageMode = SelectorOptions.getInstance().isMultiSelectable();
        View multiImageLayout = view.findViewById(R.id.multi_picker_layout);
        // 多选模式下 展示下方布局
        multiImageLayout.setVisibility(isMultiImageMode ? View.VISIBLE : View.GONE);
        if (isMultiImageMode) {
            mPreBtn = (Button) view.findViewById(R.id.choose_preview_btn);
            mOkBtn = (Button) view.findViewById(R.id.choose_ok_btn);

            mPreBtn.setOnClickListener(this);
            mOkBtn.setOnClickListener(this);
            updateMultiPickerLayoutState(mMediaAdapter.getSelectedMedias());
        }
    }

    private void initRecycleView() {
        GridLayoutManager gridLayoutManager = new HackyGridLayoutManager(getActivity(), GRID_COUNT);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        mRecycleView.setLayoutManager(gridLayoutManager);
        mRecycleView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelOffset(R.dimen.v_selector_ui_impl_media_margin), GRID_COUNT));
        mMediaAdapter.setOnCameraClickListener(new OnCameraClickListener());
        mMediaAdapter.setOnCheckedListener(new OnMediaCheckedListener());
        mMediaAdapter.setOnMediaClickListener(new OnMediaClickListener());
        mRecycleView.setAdapter(mMediaAdapter);
        mRecycleView.addOnScrollListener(new ScrollListener());
    }

    @Override
    public void showMedia(@Nullable List<MediaEntity> medias, int allCount) {
        if (medias == null || isEmptyData(medias)
                && isEmptyData(mMediaAdapter.getAllMedias())) {
            showEmptyData();
            return;
        }
        showData();
        mMediaAdapter.addAllData(medias);
        checkSelectedMedia(medias, mMediaAdapter.getSelectedMedias());
    }

    private boolean isEmptyData(List<MediaEntity> medias) {
        return medias.isEmpty() && !SelectorOptions.getInstance().isNeedCamera();
    }

    private void showEmptyData() {
        mLoadingView.setVisibility(View.GONE);
        mEmptyTxt.setVisibility(View.VISIBLE);
        mRecycleView.setVisibility(View.GONE);
    }

    private void showData() {
        mLoadingView.setVisibility(View.GONE);
        mEmptyTxt.setVisibility(View.GONE);
        mRecycleView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAlbum(@Nullable List<AlbumEntity> albums) {
        if ((albums == null || albums.isEmpty())
                && mTitleTxt != null) {
            mTitleTxt.setCompoundDrawables(null, null, null, null);
            mTitleTxt.setOnClickListener(null);
            return;
        }
        mAlbumWindowAdapter.addAllData(albums);
    }

    public MediaAdapter getMediaAdapter() {
        return mMediaAdapter;
    }

    @Override
    public void clearMedia() {
        mMediaAdapter.clearData();
    }

    private void updateMultiPickerLayoutState(List<MediaEntity> medias) {
        updateOkBtnState(medias);
        updatePreviewBtnState(medias);
    }

    private void updatePreviewBtnState(List<MediaEntity> medias) {
        if (mPreBtn == null || medias == null) {
            return;
        }
        boolean enabled = medias.size() > 0 && medias.size() <= mMaxCount;
        mPreBtn.setEnabled(enabled);
    }

    private void updateOkBtnState(List<MediaEntity> medias) {
        if (mOkBtn == null || medias == null) {
            return;
        }
        boolean enabled = medias.size() > 0 && medias.size() <= mMaxCount;
        mOkBtn.setEnabled(enabled);
        mOkBtn.setText(enabled ? getString(R.string.v_selector_ui_impl_image_select_ok_fmt,
                String.valueOf(medias.size())
                , String.valueOf(mMaxCount)) : getString(R.string.v_selector_ui_impl_ok));
    }

    @Override
    public void onCameraFinish(MediaEntity media) {
        dismissProgressDialog();
        mIsCamera = false;
        if (media == null) {
            return;
        }
        if (mMediaAdapter != null && mMediaAdapter.getSelectedMedias() != null) {
            List<MediaEntity> selectedMedias = mMediaAdapter.getSelectedMedias();
            selectedMedias.add(media);
            onFinish(selectedMedias);
        }
    }

    @Override
    public void onCameraError() {
        mIsCamera = false;
        dismissProgressDialog();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.choose_ok_btn) {
            onFinish(mMediaAdapter.getSelectedMedias());
        } else if (id == R.id.choose_preview_btn) {
            if (!mIsPreview) {
                mIsPreview = true;
                final ArrayList<MediaEntity> medias =
                        (ArrayList<MediaEntity>) mMediaAdapter.getSelectedMedias();
                Intent intent = new Intent(getActivity(), MediaPreViewActivity.class);
                MediaSelector.of(getActivity())
                        .withOptions(SelectorOptions.getInstance()
                                .setViewMode(SelectorViewModeConstants.VIEW_MODE_PREVIEW_EDIT))
                        .withIntent(intent)
                        .start(MediaFragment.IMAGE_PREVIEW_REQUEST_CODE, medias,
                                new MediaSelectCallback() {

                                    @Override
                                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                                     List<MediaEntity> medias) {
                                        if (data == null) {
                                            return;
                                        }
                                        if (resultCode == Activity.RESULT_OK) {
                                            mIsPreview = false;
                                            boolean isBackClick =
                                                    data.getBooleanExtra(MediaPreViewActivity.EXTRA_TYPE_BACK, false);
                                            List<MediaEntity> selectedMedias =
                                                    data.getParcelableArrayListExtra(MediaSelector.EXTRA_SELECTED_MEDIA);
                                            onViewActivityRequest(selectedMedias,
                                                    mMediaAdapter.getAllMedias(), isBackClick);
                                            if (isBackClick) {
                                                mMediaAdapter.setSelectedMedias(selectedMedias);
                                            }
                                            updateMultiPickerLayoutState(selectedMedias);
                                        }
                                    }

                                    @Override
                                    public void onMediaSelectError(Throwable mediaSelectError) {
                                        MediaSelectorLogUtils.d(mediaSelectError.getLocalizedMessage());
                                    }
                                });
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onViewActivityRequest(List<MediaEntity> selectedMedias,
                                       List<MediaEntity> allMedias, boolean isBackClick) {
        if (isBackClick) {
            checkSelectedMedia(allMedias, selectedMedias);
        } else {
            onFinish(selectedMedias);
        }
    }


    @Override
    public void onCameraActivityResult(int requestCode, int resultCode) {
        showProgressDialog();
        super.onCameraActivityResult(requestCode, resultCode);
    }

    private void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setIndeterminate(true);
            mDialog.setMessage(getString(R.string.v_selector_ui_impl_handling));
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
            mDialog.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<MediaEntity> medias =
                (ArrayList<MediaEntity>) getMediaAdapter().getSelectedMedias();
        onSaveMedias(outState, medias);
    }

    public void setTitleTxt(TextView titleTxt) {
        mTitleTxt = titleTxt;
        mTitleTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAlbumPopWindow == null) {
                    int height = WindowManagerHelper.getScreenHeight(v.getContext()) -
                            (WindowManagerHelper.getToolbarHeight(v.getContext())
                                    + WindowManagerHelper.getStatusBarHeight(v.getContext()));
                    View windowView = createWindowView();
                    mAlbumPopWindow = new PopupWindow(windowView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            height, true);
                    mAlbumPopWindow.setAnimationStyle(R.style.VSelectorUiImpl_Media_PopupAnimation);
                    mAlbumPopWindow.setOutsideTouchable(true);
                    mAlbumPopWindow.setBackgroundDrawable(new ColorDrawable
                            (ContextCompat.getColor(v.getContext(),
                                    R.color.v_selector_ui_impl_colorPrimaryAlpha)));
                    mAlbumPopWindow.setContentView(windowView);
                }
                mAlbumPopWindow.showAsDropDown(v, 0, 0);
            }

            @NonNull
            private View createWindowView() {
                View view =
                        LayoutInflater.from(getActivity()).inflate(R.layout.v_selector_ui_impl_layout_album, null);
                RecyclerView recyclerView =
                        (RecyclerView) view.findViewById(R.id.album_recycleview);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                        LinearLayoutManager.VERTICAL, false));
                recyclerView.addItemDecoration(new SpacesItemDecoration(2, 1));

                View albumShadowLayout = view.findViewById(R.id.album_shadow);
                albumShadowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissAlbumWindow();
                    }
                });
                mAlbumWindowAdapter.setAlbumOnClickListener(new OnAlbumItemOnClickListener());
                recyclerView.setAdapter(mAlbumWindowAdapter);
                return view;
            }
        });
    }

    private void dismissAlbumWindow() {
        if (mAlbumPopWindow != null && mAlbumPopWindow.isShowing()) {
            mAlbumPopWindow.dismiss();
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            final int childCount = recyclerView.getChildCount();
            if (childCount > 0) {
                View lastChild = recyclerView.getChildAt(childCount - 1);
                RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
                int lastVisible = recyclerView.getChildAdapterPosition(lastChild);
                if (lastVisible == outerAdapter.getItemCount() - 1 && hasNextPage() && canLoadNextPage()) {
                    onLoadNextPage();
                }
            }
        }
    }

    private class OnMediaClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MediaEntity media = (MediaEntity) v.getTag();
            int pos = (int) v.getTag(R.id.media_item_check);
            int mode = SelectorOptions.getInstance().getMode();
            if (mode == MediaTypeConstants.MEDIA_TYPE_IMAGE) {
                if (SelectorOptions.getInstance().isMultiSelectable()) {
                    multiImageClick(pos);
                } else {
                    singleImageClick(media);
                }
            } else if (mode == MediaTypeConstants.MEDIA_TYPE_VIDEO) {
                if (SelectorOptions.getInstance().isMultiSelectable()) {
                    multiVideoClick(pos);
                } else {
                    singleVideoClick(media);
                }
            }
        }

        private void multiVideoClick(int pos) {
            if (!mIsPreview) {
                AlbumEntity albumMedia = mAlbumWindowAdapter.getCurrentAlbum();
                String albumId = albumMedia != null ? albumMedia.mBucketId :
                        AlbumEntity.DEFAULT_NAME;
                mIsPreview = true;

                ArrayList<MediaEntity> medias =
                        (ArrayList<MediaEntity>) mMediaAdapter.getSelectedMedias();

                Intent intent = new Intent(getContext(), MediaPreViewActivity.class);
                MediaSelector.of(getActivity())
                        .withOptions(SelectorOptions.getInstance()
                                .setViewMode(SelectorViewModeConstants.VIEW_MODE_EDIT))
                        .withIntent(intent)
                        .start(MediaFragment.IMAGE_PREVIEW_REQUEST_CODE, medias, pos, albumId
                                , new MediaSelectCallback() {

                                    @Override
                                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                                     List<MediaEntity> medias) {
                                        if (data == null) {
                                            return;
                                        }
                                        if (resultCode == Activity.RESULT_OK) {
                                            mIsPreview = false;
                                            boolean isBackClick =
                                                    data.getBooleanExtra(MediaPreViewActivity.EXTRA_TYPE_BACK, false);
                                            List<MediaEntity> selectedMedias =
                                                    data.getParcelableArrayListExtra(MediaSelector.EXTRA_SELECTED_MEDIA);
                                            onViewActivityRequest(selectedMedias,
                                                    mMediaAdapter.getAllMedias(), isBackClick);
                                            if (isBackClick) {
                                                mMediaAdapter.setSelectedMedias(selectedMedias);
                                            }
                                            updateMultiPickerLayoutState(selectedMedias);
                                        }
                                    }

                                    @Override
                                    public void onMediaSelectError(Throwable mediaSelectError) {
                                        MediaSelectorLogUtils.d(mediaSelectError.getLocalizedMessage());
                                    }
                                });
            }
        }

        private void singleVideoClick(MediaEntity media) {
            ArrayList<MediaEntity> iMedias = new ArrayList<>();
            iMedias.add(media);
            onFinish(iMedias);
        }

        private void multiImageClick(int pos) {
            if (!mIsPreview) {
                AlbumEntity albumMedia = mAlbumWindowAdapter.getCurrentAlbum();
                String albumId = albumMedia != null ? albumMedia.mBucketId :
                        AlbumEntity.DEFAULT_NAME;
                mIsPreview = true;

                ArrayList<MediaEntity> medias =
                        (ArrayList<MediaEntity>) mMediaAdapter.getSelectedMedias();

                Intent intent = new Intent(getContext(), MediaPreViewActivity.class);
                MediaSelector.of(getActivity())
                        .withOptions(SelectorOptions.getInstance()
                                .setViewMode(SelectorViewModeConstants.VIEW_MODE_EDIT))
                        .withIntent(intent)
                        .start(MediaFragment.IMAGE_PREVIEW_REQUEST_CODE, medias, pos, albumId
                                , new MediaSelectCallback() {

                                    @Override
                                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                                     List<MediaEntity> medias) {
                                        if (data == null) {
                                            return;
                                        }
                                        if (resultCode == Activity.RESULT_OK) {
                                            mIsPreview = false;
                                            boolean isBackClick =
                                                    data.getBooleanExtra(MediaPreViewActivity.EXTRA_TYPE_BACK, false);
                                            List<MediaEntity> selectedMedias =
                                                    data.getParcelableArrayListExtra(MediaSelector.EXTRA_SELECTED_MEDIA);
                                            onViewActivityRequest(selectedMedias,
                                                    mMediaAdapter.getAllMedias(), isBackClick);
                                            if (isBackClick) {
                                                mMediaAdapter.setSelectedMedias(selectedMedias);
                                            }
                                            updateMultiPickerLayoutState(selectedMedias);
                                        }
                                    }

                                    @Override
                                    public void onMediaSelectError(Throwable mediaSelectError) {
                                        MediaSelectorLogUtils.d(mediaSelectError.getLocalizedMessage());
                                    }
                                });
            }
        }

        private void singleImageClick(MediaEntity media) {
            ArrayList<MediaEntity> iMedias = new ArrayList<>();
            iMedias.add(media);
            onFinish(iMedias);
        }
    }


    private class OnCameraClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!mIsCamera) {
                mIsCamera = true;
                startCamera(getActivity(), MediaFragment.this, MediaFileHelper.getSubDir());
            }
        }
    }

    private class OnMediaCheckedListener implements MediaAdapter.OnMediaCheckedListener {

        @Override
        public void onChecked(View view, MediaEntity media) {
            boolean isSelected = !media.isSelected();
            MediaItemLayout layout = (MediaItemLayout) view;
            List<MediaEntity> selectedMedias = mMediaAdapter.getSelectedMedias();
            if (isSelected) {
                if (media instanceof ImageMediaEntity) {
                    ImageMediaEntity photoMedia = (ImageMediaEntity) media;
                    if (selectedMedias.size() >= mMaxCount) {
                        String warning = getString(R.string.v_selector_ui_impl_too_many_picture_fmt,
                                mMaxCount);
                        Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!selectedMedias.contains(photoMedia)) {
                        if (photoMedia.isGif() && photoMedia.getSize() > SelectorOptions.getInstance().getMaxGifSize()) {
                            Toast.makeText(getActivity(), R.string.v_selector_ui_impl_gif_too_big,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectedMedias.add(photoMedia);
                    }
                } else if (media instanceof VideoMediaEntity){
                    VideoMediaEntity videoMedia = (VideoMediaEntity) media;
                    if (selectedMedias.size() >= mMaxCount) {
                        String warning = getString(R.string.v_selector_ui_impl_too_many_picture_fmt,
                                mMaxCount);
                        Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!selectedMedias.contains(videoMedia)) {
                        selectedMedias.add(videoMedia);
                    }
                }
            } else {
                if (selectedMedias.size() >= 1 && selectedMedias.contains(media)) {
                    selectedMedias.remove(media);
                }
            }
            media.setSelected(isSelected);
            layout.setChecked(isSelected);
            updateMultiPickerLayoutState(selectedMedias);
        }
    }

    private class OnAlbumItemOnClickListener implements AlbumAdapter.OnAlbumClickListener {

        @Override
        public void onClick(View view, int pos) {
            AlbumAdapter adapter = mAlbumWindowAdapter;
            if (adapter != null && adapter.getCurrentAlbumPos() != pos) {
                List<AlbumEntity> albums = adapter.getAlums();
                adapter.setCurrentAlbumPos(pos);

                AlbumEntity albumMedia = albums.get(pos);
                loadMedias(0, albumMedia.mBucketId);
                mTitleTxt.setText(albumMedia.mBucketName == null ?
                        getString(R.string.v_selector_ui_impl_default_album_name) :
                        albumMedia.mBucketName);

                for (AlbumEntity album : albums) {
                    album.mIsSelected = false;
                }
                albumMedia.mIsSelected = true;
                adapter.notifyDataSetChanged();
            }
            dismissAlbumWindow();
        }
    }

}
