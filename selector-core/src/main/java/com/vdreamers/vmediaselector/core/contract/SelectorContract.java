package com.vdreamers.vmediaselector.core.contract;

import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;

import java.util.List;

/**
 * 选择器契约类
 * This specifies the contract between the view and the presenter.
 * <p>
 * date 2019-09-18 20:23:56
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public interface SelectorContract {

    /**
     * 视图层接口
     * define the functions of the view, interacting with presenter
     */
    interface View {
        /**
         * 给视图层设置逻辑层
         * set the presenter attaching to the view
         *
         * @param presenter 逻辑层
         */
        void setPresenter(@NonNull Presenter presenter);

        /**
         * 展示多媒体列表
         * show a list  the {@link MediaEntity} in the view
         *
         * @param medias   多媒体列表
         * @param allCount 总数
         */
        void showMedia(@Nullable List<MediaEntity> medias, int allCount);

        /**
         * 展示相册列表
         * show all the {@link AlbumEntity} in the view
         *
         * @param albums 相册列表
         */
        void showAlbum(@Nullable List<AlbumEntity> albums);

        /**
         * 获取内容解析器
         * get the {@link ContentResolver} in the view
         *
         * @return 内容解析器
         */
        @NonNull
        ContentResolver getAppCr();

        /**
         * 多媒体选择结束
         * call when the view should be finished or the process is finished
         *
         * @param medias 多媒体选择结果
         */
        void onFinish(@NonNull List<MediaEntity> medias);

        /**
         * 清理视图中的所有多媒体类
         * clear all the {@link MediaEntity} in the view
         */
        void clearMedia();
    }

    /**
     * 逻辑层接口
     * define the function of presenter, to control the module to load data and to tell view to
     * displayRaw the data
     */
    interface Presenter {
        /**
         * 加载具体多媒体数据
         * load the specify data from {@link ContentResolver}
         *
         * @param page    需要加载的页数索引 the page need to load
         * @param albumId 相册Id album albumId
         */
        void loadMedias(int page, String albumId);

        /**
         * 加载相册列表
         * load all the album from {@link ContentResolver}
         */
        void loadAlbums();

        /**
         * 销毁逻辑层，将视图层置空
         * destroy the presenter and set the view null
         */
        void destroy();

        /**
         * 是否有下一页数据
         * has more data to load
         *
         * @return true:有 false:没有
         */
        boolean hasNextPage();

        /**
         * 能否加载下一页数据
         *
         * @return true:能 false:不能
         */
        boolean canLoadNextPage();

        /**
         * 正在加载下一页数据
         * load next page
         */
        void onLoadNextPage();

        /**
         * 设置已选中的多媒体列表
         * Determine the selected allMedias according to mSelectedMedias
         *
         * @param allMedias      all medias 所有多媒体列表
         * @param selectedMedias the medias to be selected 选中的多媒体列表
         */
        void checkSelectedMedia(List<MediaEntity> allMedias, List<MediaEntity> selectedMedias);
    }
}
