package com.vdreamers.vmediaselector.core.impl.task.impl;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.impl.callback.IAlbumTaskCallback;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.scope.MimeTypeConstants;
import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 相册加载任务
 * <p>
 * date 2019-09-18 20:31:50
 *`
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@WorkerThread
public class AlbumTask {

    /**
     * 相册Map key为文件夹Id
     */
    private Map<String, AlbumEntity> mBucketMap;
    /**
     * 默认相册
     */
    private AlbumEntity mDefaultAlbum;
    /**
     * 未知相册名
     */
    private static final String UNKNOWN_ALBUM_NAME = "unknown";
    /**
     * 未知相册数量
     */
    private int mUnknownAlbumNumber = 1;
    /**
     * 选择器选项参数
     */
    private SelectorOptions mSelectorOptions;
    /**
     * 查询Uri
     */
    private static final Uri QUERY_URI = Media.EXTERNAL_CONTENT_URI;

    private static final String SQL_ARG = "=?";
    private static final String SQL_OR = " OR ";
    private static final String SQL_AND = " AND ";

    /**
     * 相册查询列
     */
    private static final String[] QUERY_PROJECTION_ALBUM = {
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME};
    /**
     * 相册查询条件
     */
    private static final String QUERY_SELECTION_ALBUM = "0==0)" +
            " GROUP BY (" + Media.BUCKET_ID;
    /**
     * 相册排序条件
     */
    private static final String QUERY_SORT_ORDER_ALBUM = Media.DATE_MODIFIED +
            " DESC";
    /**
     * 相册封面查询列
     */
    private static final String[] QUERY_PROJECTION_ALBUM_COVER = {
            Media._ID};
    /**
     * 相册封面查询条件
     */
    private static final String SELECTION_IMAGE_MIME_TYPE = Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Media.MIME_TYPE + SQL_ARG;
    private static final String SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF = Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Media.MIME_TYPE + SQL_ARG;
    private static final String SELECTION_ID = Media.BUCKET_ID + SQL_ARG +
            SQL_AND + "( " + SELECTION_IMAGE_MIME_TYPE + " )";
    private static final String SELECTION_ID_WITHOUT_GIF = Media.BUCKET_ID + SQL_ARG +
            SQL_AND + "( " + SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF + " )";
    private static final String[] SELECTION_ARGS_IMAGE_MIME_TYPE = {
            MimeTypeConstants.IMAGE_JPEG,
            MimeTypeConstants.IMAGE_PNG,
            MimeTypeConstants.IMAGE_JPG,
            MimeTypeConstants.IMAGE_GIF};
    private static final String[] SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF = {
            MimeTypeConstants.IMAGE_JPEG,
            MimeTypeConstants.IMAGE_PNG,
            MimeTypeConstants.IMAGE_JPG};
    /**
     * 相册封面排序条件
     */
    private static final String QUERY_SORT_ORDER_ALBUM_COVER = Media.DATE_MODIFIED
            + " DESC";

    public AlbumTask() {
        this.mBucketMap = new ArrayMap<>();
        this.mDefaultAlbum = AlbumEntity.createDefaultAlbum();
        this.mSelectorOptions = SelectorOptions.getInstance();
    }

    public void start(@NonNull final ContentResolver cr,
                      @NonNull final IAlbumTaskCallback callback) {
        buildAlbumInfo(cr);
        getAlbumList(callback);
    }

    /**
     * 构造相册信息
     *
     * @param cr ContentResolver
     */
    private void buildAlbumInfo(ContentResolver cr) {
        Cursor bucketCursor = null;
        try {
            bucketCursor = cr.query(QUERY_URI, QUERY_PROJECTION_ALBUM, QUERY_SELECTION_ALBUM,
                    null, QUERY_SORT_ORDER_ALBUM);
            if (bucketCursor != null && bucketCursor.moveToFirst()) {
                do {
                    String buckId =
                            bucketCursor.getString(bucketCursor.getColumnIndex(QUERY_PROJECTION_ALBUM[0]));
                    String name =
                            bucketCursor.getString(bucketCursor.getColumnIndex(QUERY_PROJECTION_ALBUM[1]));
                    AlbumEntity album = buildAlbumInfo(name, buckId);
                    if (!TextUtils.isEmpty(buckId)) {
                        buildAlbumCover(cr, buckId, album);
                    }
                } while (bucketCursor.moveToNext());
            }
        } finally {
            if (bucketCursor != null) {
                bucketCursor.close();
            }
        }
    }

    /**
     * 获取相册封面和相册照片数量
     *
     * @param buckId album id
     */
    private void buildAlbumCover(ContentResolver cr, String buckId, AlbumEntity album) {
        boolean isNeedGif = mSelectorOptions != null && mSelectorOptions.isNeedGif();
        String selectionId = isNeedGif ? SELECTION_ID : SELECTION_ID_WITHOUT_GIF;
        String[] args = isNeedGif ? SELECTION_ARGS_IMAGE_MIME_TYPE :
                SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF;
        String[] selectionArgs = new String[args.length + 1];
        selectionArgs[0] = buckId;
        for (int i = 1; i < selectionArgs.length; i++) {
            selectionArgs[i] = args[i - 1];
        }
        Cursor coverCursor = cr.query(QUERY_URI, QUERY_PROJECTION_ALBUM_COVER, selectionId,
                selectionArgs, QUERY_SORT_ORDER_ALBUM_COVER);
        try {
            if (coverCursor != null && coverCursor.moveToFirst()) {
                String picId =
                        coverCursor.getString(coverCursor.getColumnIndex(QUERY_PROJECTION_ALBUM_COVER[0]));
                album.mCount = coverCursor.getCount();
                // Uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , Long.parseLong(picId));
                album.mImageList.add(ImageMediaEntity.of().setId(picId).setUri(uri));
                if (album.mImageList.size() > 0) {
                    mBucketMap.put(buckId, album);
                }
            }
        } finally {
            if (coverCursor != null) {
                coverCursor.close();
            }
        }
    }

    /**
     * 相册列表
     *
     * @param callback 相册加载任务回调
     */
    private void getAlbumList(@NonNull final IAlbumTaskCallback callback) {
        mDefaultAlbum.mCount = 0;
        List<AlbumEntity> tmpList = new ArrayList<>();
        if (mBucketMap == null) {
            postAlbums(callback, tmpList);
            return;
        }
        for (Map.Entry<String, AlbumEntity> entry : mBucketMap.entrySet()) {
            tmpList.add(entry.getValue());
            mDefaultAlbum.mCount += entry.getValue().mCount;
        }
        if (tmpList.size() > 0 && tmpList.get(0) != null) {
            mDefaultAlbum.mImageList = tmpList.get(0).mImageList;
            tmpList.add(0, mDefaultAlbum);
        }
        postAlbums(callback, tmpList);
        clear();
    }

    /**
     * 返回相册列表
     *
     * @param callback 相册加载任务回调
     * @param result   相册列表
     */
    private void postAlbums(@NonNull final IAlbumTaskCallback callback,
                            final List<AlbumEntity> result) {
        // 在UI线程返回
        LoadExecutorUtils.getInstance().runUI(new Runnable() {
            @Override
            public void run() {
                callback.postAlbumList(result);
            }
        });
    }

    /**
     * 构造相册信息
     *
     * @param bucketName 相册文件夹名
     * @param bucketId   相册文件Id名
     * @return 相册信息
     */
    @NonNull
    private AlbumEntity buildAlbumInfo(String bucketName, String bucketId) {
        AlbumEntity album = null;
        if (!TextUtils.isEmpty(bucketId)) {
            album = mBucketMap.get(bucketId);
        }
        if (album == null) {
            album = new AlbumEntity();
            if (!TextUtils.isEmpty(bucketId)) {
                album.mBucketId = bucketId;
            } else {
                album.mBucketId = String.valueOf(mUnknownAlbumNumber);
                mUnknownAlbumNumber++;
            }
            if (!TextUtils.isEmpty(bucketName)) {
                album.mBucketName = bucketName;
            } else {
                album.mBucketName = UNKNOWN_ALBUM_NAME;
                mUnknownAlbumNumber++;
            }
            if (album.mImageList.size() > 0) {
                mBucketMap.put(bucketId, album);
            }
        }
        return album;
    }

    /**
     * clear
     */
    private void clear() {
        if (mBucketMap != null) {
            mBucketMap.clear();
        }
    }

}
