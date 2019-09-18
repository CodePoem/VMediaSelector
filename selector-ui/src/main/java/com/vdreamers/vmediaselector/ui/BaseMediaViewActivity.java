package com.vdreamers.vmediaselector.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vdreamers.vmediaselector.core.contract.SelectorContract;
import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.loader.IMediaCallback;
import com.vdreamers.vmediaselector.core.loader.MediaLoader;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * 视图层-自定义多媒体View的抽象Activity类（查看大图）
 * A abstract class which implements {@link SelectorContract.View} for custom media view.
 * For view big images.
 * <p>
 * date 2019-09-18 20:20:19
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("unused")
public abstract class BaseMediaViewActivity extends AppCompatActivity implements SelectorContract.View {
    ArrayList<MediaEntity> mSelectedImages;
    String mAlbumId;
    int mStartPos;

    private SelectorContract.Presenter mPresenter;

    /**
     * start loading when the permission request is completed.
     * call {@link #loadMedias()} or {@link #loadMedias(int, String)}.
     */
    public abstract void startLoading();

    /**
     * override this method to handle the medias.
     * make sure {@link #loadMedias()} ()} being called first.
     *
     * @param medias the results of medias
     */
    @Override
    public void showMedia(@Nullable List<MediaEntity> medias, int allCount) {
    }

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
        super.onCreate(savedInstanceState);
        parseSelectedMedias(savedInstanceState, getIntent());
        setPresenter(createPresenter(this));
    }

    private void parseSelectedMedias(Bundle savedInstanceState, Intent intent) {
        if (savedInstanceState != null) {
            mSelectedImages =
                    savedInstanceState.getParcelableArrayList(MediaSelector.EXTRA_SELECTED_MEDIA);
            mAlbumId = savedInstanceState.getString(MediaSelector.EXTRA_ALBUM_ID);
            mStartPos = savedInstanceState.getInt(MediaSelector.EXTRA_START_POS, 0);
        } else if (intent != null) {
            mStartPos = intent.getIntExtra(MediaSelector.EXTRA_START_POS, 0);
            mSelectedImages =
                    intent.getParcelableArrayListExtra(MediaSelector.EXTRA_SELECTED_MEDIA);
            mAlbumId = intent.getStringExtra(MediaSelector.EXTRA_ALBUM_ID);
        }
    }

    @Override
    public final void setPresenter(@NonNull SelectorContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * 根据视图层创建逻辑层
     *
     * @param view 视图层
     * @return 逻辑层
     */
    public abstract SelectorContract.Presenter createPresenter(SelectorContract.View view);

    /**
     * get the {@link ContentResolver}
     */
    @NonNull
    @Override
    public final ContentResolver getAppCr() {
        return getApplicationContext().getContentResolver();
    }

    public final void loadRawImage(@NonNull ImageView img, @NonNull MediaEntity media, int width,
                                   int height, IMediaCallback callback) {
        MediaLoader.getInstance().displayRaw(img, media, width, height, callback);
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
     * get the max count set before
     */
    public final int getMaxCount() {
        SelectorOptions options = SelectorOptions.getInstance();
        if (options == null) {
            return SelectorOptions.DEFAULT_MAX_SELECT_NUM;
        }
        return options.getMaxSelectNum();
    }

    @NonNull
    public final ArrayList<MediaEntity> getSelectedImages() {
        if (mSelectedImages != null) {
            return mSelectedImages;
        }
        return new ArrayList<>();
    }

    public final String getAlbumId() {
        return mAlbumId;
    }

    public final int getStartPos() {
        return mStartPos;
    }
}
