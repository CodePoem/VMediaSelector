package com.vdreamers.vmediaselector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.vdreamers.vmediaselector.core.callback.OnMediaSelectorFinishListener;
import com.vdreamers.vmediaselector.core.contract.SelectorContract;
import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;


/**
 * 视图层-自定义多媒体View抽象Fragment类
 * A abstract class which implements {@link SelectorContract.View} for custom media view.
 * only one methods need to override {@link #startLoading()}, but there is more function to
 * achieve by
 * checking every method can override.
 * <p>
 * date 2019-09-18 22:00:47
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseMediaViewFragment extends Fragment implements SelectorContract.View {
    public static final String[] STORAGE_PERMISSIONS =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA};

    private static final int REQUEST_CODE_PERMISSION = 8023;

    private SelectorContract.Presenter mPresenter;
    private CameraPickerHelper mCameraPicker;
    private OnMediaSelectorFinishListener mOnFinishListener;

    /**
     * start loading when the permission request is completed.
     * call {@link #loadMedias()} or {@link #loadMedias(int, String)}, call {@link #loadAlbum()}
     * if albums needed.
     */
    public abstract void startLoading();

    /**
     * called when request {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} and
     * {@link Manifest.permission#CAMERA} permission error.
     *
     * @param e a IllegalArgumentException, IllegalStateException or SecurityException will be throw
     */
    public void onRequestPermissionError(String[] permissions, Exception e) {
    }

    /**
     * called when request {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} and
     * {@link Manifest.permission#CAMERA} permission successfully.
     */
    public void onRequestPermissionSuc(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    }

    /**
     * get the result of using camera to take a photo.
     *
     * @param media {@link MediaEntity}
     */
    public void onCameraFinish(MediaEntity media) {
    }

    /**
     * called when camera start error
     */
    public void onCameraError() {
    }

    /**
     * must override when care about the input medias, which means you call
     * {@link #setSelectedBundle(ArrayList)} first.
     * this method is called in {@link Fragment#onCreate(Bundle)}, so override this rather than
     * {@link Fragment#onCreate(Bundle)}.
     *
     * @param bundle         If the fragment is being re-created from
     *                       a previous saved state, this is the state.
     * @param selectedMedias the input medias, the parameter of
     *                       {@link #setSelectedBundle(ArrayList)}.
     */
    public void onCreateWithSelectedMedias(Bundle bundle,
                                           @Nullable List<MediaEntity> selectedMedias) {
    }

    /**
     * override this method to handle the medias.
     * make sure {@link #loadMedias()} ()} being called first.
     *
     * @param medias the results of medias
     */
    @Override
    public void showMedia(@Nullable List<MediaEntity> medias, int allCount) {
    }

    /**
     * override this method to handle the album.
     * make sure {@link #loadAlbum()} being called first.
     *
     * @param albums the results of albums
     */
    @Override
    public void showAlbum(@Nullable List<AlbumEntity> albums) {
    }

    /**
     * to clear all medias the first time(the page number is 0). do some clean work.
     */
    @Override
    public void clearMedia() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        onCreateWithSelectedMedias(savedInstanceState, parseSelectedMedias(savedInstanceState,
                getArguments()));
        super.onCreate(savedInstanceState);

        initCameraPhotoPicker(savedInstanceState);
    }

    @Nullable
    private ArrayList<MediaEntity> parseSelectedMedias(Bundle savedInstanceState, Bundle argument) {
        ArrayList<MediaEntity> selectedMedias = null;
        if (savedInstanceState != null) {
            selectedMedias =
                    savedInstanceState.getParcelableArrayList(MediaSelector.EXTRA_SELECTED_MEDIA);
        } else if (argument != null) {
            selectedMedias = argument.getParcelableArrayList(MediaSelector.EXTRA_SELECTED_MEDIA);
        }
        return selectedMedias;
    }

    private void initCameraPhotoPicker(Bundle savedInstanceState) {
        if (SelectorOptions.getInstance() == null || !SelectorOptions.getInstance().isNeedCamera()) {
            return;
        }
        mCameraPicker = new CameraPickerHelper(savedInstanceState);
        mCameraPicker.setPickCallback(new CameraListener(this));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPermissionAndLoad();
    }

    private void checkPermissionAndLoad() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(getActivity(), STORAGE_PERMISSIONS[0]) != PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), STORAGE_PERMISSIONS[1]) != PERMISSION_GRANTED) {
                requestPermissions(STORAGE_PERMISSIONS, REQUEST_CODE_PERMISSION);
            } else {
                startLoading();
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            onRequestPermissionError(STORAGE_PERMISSIONS, e);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (REQUEST_CODE_PERMISSION == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRequestPermissionSuc(requestCode, permissions, grantResults);
            } else {
                onRequestPermissionError(permissions,
                        new SecurityException("request android.permission.READ_EXTERNAL_STORAGE " +
                                "error."));
            }
        }
    }

    /**
     * called when you have input medias, then call
     * {@link #onCreateWithSelectedMedias(Bundle, List)} to get the input medias.
     *
     * @param selectedMedias input medias
     * @return {@link BaseMediaViewFragment}
     */
    public final BaseMediaViewFragment setSelectedBundle(ArrayList<MediaEntity> selectedMedias) {
        Bundle bundle = new Bundle();
        if (selectedMedias != null && !selectedMedias.isEmpty()) {
            bundle.putParcelableArrayList(MediaSelector.EXTRA_SELECTED_MEDIA, selectedMedias);
        }
        setArguments(bundle);
        return this;
    }

    @Override
    public final void setPresenter(@NonNull SelectorContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * get the {@link ContentResolver}
     */
    @NonNull
    @Override
    public final ContentResolver getAppCr() {
        return getActivity().getApplicationContext().getContentResolver();
    }

    /**
     * if {@link BaseMediaViewFragment} is not working with {@link BaseMediaActivity}, it needs a
     * listener to call
     * when the jobs done.
     *
     * @param onFinishListener {@link OnMediaSelectorFinishListener}
     */
    final void setOnFinishListener(OnMediaSelectorFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    /**
     * called the job is done.Click the ok button, take a photo from camera.
     * most of the time, you do not have to override.
     *
     * @param medias the list of selection
     */
    @Override
    public void onFinish(@NonNull List<MediaEntity> medias) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(MediaSelector.EXTRA_RESULT,
                (ArrayList<MediaEntity>) medias);
        if (mOnFinishListener != null) {
            mOnFinishListener.onMediaSelectorFinish(intent, medias);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCameraPicker != null && requestCode == CameraPickerHelper.REQ_CODE_CAMERA) {
            onCameraActivityResult(requestCode, resultCode);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCameraPicker != null) {
            mCameraPicker.onSaveInstanceState(outState);
        }
    }

    /**
     * in MULTI_IMG call this in
     * {@link Fragment#onSaveInstanceState(Bundle)}.
     *
     * @param outState Bundle in which to place your saved state.
     * @param selected the selected medias.
     */
    public final void onSaveMedias(Bundle outState, ArrayList<MediaEntity> selected) {
        if (selected != null && !selected.isEmpty()) {
            outState.putParcelableArrayList(MediaSelector.EXTRA_SELECTED_MEDIA, selected);
        }
    }

    /**
     * call this to clear resource.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        if (mCameraPicker != null) {
            mCameraPicker.release();
        }
    }

    /**
     * in MULTI_IMG, call this to pick the selected medias in all medias.
     */
    public final void checkSelectedMedia(List<MediaEntity> allMedias,
                                         List<MediaEntity> selectedMedias) {
        mPresenter.checkSelectedMedia(allMedias, selectedMedias);
    }

    /**
     * load first page of medias.
     * use {@link #showMedia(List, int)} to get the result.
     */
    public final void loadMedias() {
        mPresenter.loadMedias(0, AlbumEntity.DEFAULT_NAME);
    }

    /**
     * load the medias for the specify page and album id.
     * use {@link #showMedia(List, int)} to get the result.
     *
     * @param page    page numbers.
     * @param albumId the album id is {@link AlbumEntity#mBucketId}.
     */
    public final void loadMedias(int page, String albumId) {
        mPresenter.loadMedias(page, albumId);
    }

    /**
     * extra call to load albums in database, use {@link #showAlbum(List)} to get result.
     * In MODE_VIDEO it is not necessary.
     */
    public void loadAlbum() {
        if (!SelectorOptions.getInstance().isVideoMode()) {
            mPresenter.loadAlbums();
        }
    }

    public final boolean hasNextPage() {
        return mPresenter.hasNextPage();
    }

    public final boolean canLoadNextPage() {
        return mPresenter.canLoadNextPage();
    }

    public final void onLoadNextPage() {
        mPresenter.onLoadNextPage();
    }

    /**
     * get the max count set before
     */
    public final int getMaxCount() {
        SelectorOptions selectorOptions = SelectorOptions.getInstance();
        if (selectorOptions == null) {
            return SelectorOptions.DEFAULT_MAX_SELECT_NUM;
        }
        return selectorOptions.getMaxSelectNum();
    }

    /**
     * successfully get result from camera in {@link #onActivityResult(int, int, Intent)}.
     * call this after other operations.
     */
    public void onCameraActivityResult(int requestCode, int resultCode) {
        mCameraPicker.onActivityResult(requestCode, resultCode);
    }

    /**
     * start camera to take a photo.
     *
     * @param activity      the caller activity.
     * @param fragment      the caller fragment, may be null.
     * @param subFolderPath the folder name in "DCIM/mediaselector/vmediaselector/"
     */
    public final void startCamera(Activity activity, Fragment fragment, String subFolderPath) {
        try {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    CAMERA_PERMISSIONS[0]) != PERMISSION_GRANTED) {
                requestPermissions(CAMERA_PERMISSIONS, REQUEST_CODE_PERMISSION);
            } else {
                if (!SelectorOptions.getInstance().isVideoMode()) {
                    mCameraPicker.startCamera(activity, fragment, subFolderPath);
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            onRequestPermissionError(CAMERA_PERMISSIONS, e);
        }
    }

    private static final class CameraListener implements CameraPickerHelper.Callback {
        private WeakReference<BaseMediaViewFragment> mWr;

        CameraListener(BaseMediaViewFragment fragment) {
            mWr = new WeakReference<>(fragment);
        }

        @Override
        public void onFinish(@NonNull CameraPickerHelper helper) {
            BaseMediaViewFragment fragment = mWr.get();
            if (fragment == null) {
                return;
            }
            File file = new File(helper.getSourceFilePath());

            if (!file.exists()) {
                onError(helper);
                return;
            }
            ImageMediaEntity cameraMedia = new ImageMediaEntity(file);

            // 存储拍照后的照片到数据库
            if (SelectorOptions.getInstance().isStoreCameraImage()) {
                cameraMedia.saveMediaStore(fragment.getActivity(), fragment.getAppCr());
            }

            fragment.onCameraFinish(cameraMedia);
        }

        @Override
        public void onError(@NonNull CameraPickerHelper helper) {
            BaseMediaViewFragment fragment = mWr.get();
            if (fragment == null) {
                return;
            }
            fragment.onCameraError();
        }

    }
}
