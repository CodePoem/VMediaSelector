package com.vdreamers.vmediaselector.core.impl.presenter;

import android.content.ContentResolver;
import android.net.Uri;

import com.vdreamers.vmediaselector.core.contract.SelectorContract;
import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.impl.callback.IAlbumTaskCallback;
import com.vdreamers.vmediaselector.core.impl.callback.IMediaTaskCallback;
import com.vdreamers.vmediaselector.core.impl.task.LoadManager;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择器逻辑层实现
 * A presenter implement {@link SelectorContract.Presenter}.
 * <p>
 * date 2019-09-18 20:31:43
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("ConstantConditions")
public class SelectorPresenter implements SelectorContract.Presenter {
    /**
     * 选择器视图层
     */
    private SelectorContract.View mTasksView;
    /**
     * 总页数
     */
    private int mTotalPage;
    /**
     * 当前页数
     */
    private int mCurrentPage;
    /**
     * 是否正在加载下一页
     */
    private boolean mIsLoadingNextPage;
    /**
     * 当前相册Id
     */
    private String mCurrentAlbumId;
    /**
     * 多媒体选择回调
     */
    private LoadMediaCallback mLoadMediaCallback;
    /**
     * 相册选择回调
     */
    private LoadAlbumCallback mLoadAlbumCallback;

    public SelectorPresenter(SelectorContract.View tasksView) {
        this.mTasksView = tasksView;
        this.mTasksView.setPresenter(this);
        this.mLoadMediaCallback = new LoadMediaCallback(this);
        this.mLoadAlbumCallback = new LoadAlbumCallback(this);
    }

    @Override
    public void loadMedias(int page, String albumId) {
        mCurrentAlbumId = albumId;
        if (page == 0) {
            mTasksView.clearMedia();
            mCurrentPage = 0;
        }
        ContentResolver cr = mTasksView.getAppCr();
        LoadManager.getInstance().loadMedia(cr, page, albumId, mLoadMediaCallback);
    }

    @Override
    public void loadAlbums() {
        ContentResolver cr = mTasksView.getAppCr();
        LoadManager.getInstance().loadAlbum(cr, mLoadAlbumCallback);
    }

    @Override
    public void destroy() {
        mTasksView = null;
    }

    @Override
    public boolean hasNextPage() {
        return mCurrentPage < mTotalPage;
    }

    @Override
    public boolean canLoadNextPage() {
        return !mIsLoadingNextPage;
    }

    @Override
    public void onLoadNextPage() {
        mCurrentPage++;
        mIsLoadingNextPage = true;
        loadMedias(mCurrentPage, mCurrentAlbumId);
    }

    @Override
    public void checkSelectedMedia(List<MediaEntity> allMedias, List<MediaEntity> selectedMedias) {
        if (allMedias == null || allMedias.size() == 0) {
            return;
        }
        Map<Uri, ImageMediaEntity> map = new HashMap<>(allMedias.size());
        for (MediaEntity allMedia : allMedias) {
            if (!(allMedia instanceof ImageMediaEntity)) {
                return;
            }
            ImageMediaEntity media = (ImageMediaEntity) allMedia;
            media.setSelected(false);
            map.put(media.getUri(), media);
        }
        if (selectedMedias == null || selectedMedias.size() < 0) {
            return;
        }
        for (MediaEntity media : selectedMedias) {
            Uri uri = media.getUri();
            if (map.containsKey(uri)) {
                ImageMediaEntity imageMediaEntity = map.get(uri);
                if (imageMediaEntity != null) {
                    imageMediaEntity.setSelected(true);
                }
            }
        }
    }

    private static class LoadMediaCallback implements IMediaTaskCallback<MediaEntity> {
        private WeakReference<SelectorPresenter> mWr;

        LoadMediaCallback(SelectorPresenter presenter) {
            mWr = new WeakReference<>(presenter);
        }

        private SelectorPresenter getPresenter() {
            return mWr.get();
        }

        @Override
        public void postMedia(List<MediaEntity> medias, int count) {
            SelectorPresenter presenter = getPresenter();
            if (presenter == null) {
                return;
            }
            SelectorContract.View view = presenter.mTasksView;
            if (view != null) {
                view.showMedia(medias, count);
            }
            presenter.mTotalPage = count / MediaSelector.PAGE_LIMIT;
            presenter.mIsLoadingNextPage = false;
        }
    }

    private static class LoadAlbumCallback implements IAlbumTaskCallback {
        private WeakReference<SelectorPresenter> mWr;

        LoadAlbumCallback(SelectorPresenter presenter) {
            mWr = new WeakReference<>(presenter);
        }

        private SelectorPresenter getPresenter() {
            return mWr.get();
        }

        @Override
        public void postAlbumList(List<AlbumEntity> list) {
            SelectorPresenter presenter = getPresenter();
            if (presenter == null) {
                return;
            }
            if (presenter.mTasksView != null) {
                presenter.mTasksView.showAlbum(list);
            }
        }
    }

}
