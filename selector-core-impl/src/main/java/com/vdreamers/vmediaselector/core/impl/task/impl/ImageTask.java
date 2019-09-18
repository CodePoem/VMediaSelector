package com.vdreamers.vmediaselector.core.impl.task.impl;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.collection.ArrayMap;

import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.impl.callback.IMediaTaskCallback;
import com.vdreamers.vmediaselector.core.impl.task.IMediaTask;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.scope.MimeTypeConstants;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;
import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片加载任务
 * <p>
 * date 2019-09-18 20:31:53
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@WorkerThread
public class ImageTask implements IMediaTask<ImageMediaEntity> {

    /**
     * 选择器选项参数
     */
    private SelectorOptions mSelectorOptions;
    /**
     * 缩略图Map key为imageId {@link Images.Thumbnails#IMAGE_ID}
     */
    private Map<Long, Uri> mThumbnailMap;

    private static final Uri QUERY_URI_THUMBNAIL = Images.Thumbnails.EXTERNAL_CONTENT_URI;
    private static final String[] QUERY_PROJECTION_THUMBNAIL = {
            Images.Thumbnails.IMAGE_ID};

    private static final String SQL_ARG = "=?";
    private static final String SQL_OR = " OR ";
    private static final String SQL_AND = " AND ";

    private static final String SELECTION_IMAGE_MIME_TYPE = Images.Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Images.Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Images.Media.MIME_TYPE + SQL_ARG +
            SQL_OR + Images.Media.MIME_TYPE + SQL_ARG;
    private static final String SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF =
            Images.Media.MIME_TYPE + SQL_ARG +
                    SQL_OR + Images.Media.MIME_TYPE + SQL_ARG +
                    SQL_OR + Images.Media.MIME_TYPE + SQL_ARG;
    private static final String SELECTION_ID = Images.Media.BUCKET_ID + SQL_ARG +
            SQL_AND + "( " + SELECTION_IMAGE_MIME_TYPE + " )";
    private static final String SELECTION_ID_WITHOUT_GIF = Images.Media.BUCKET_ID + SQL_ARG +
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

    private static final String DESC = " DESC";

    public ImageTask() {
        this.mThumbnailMap = new ArrayMap<>();
        this.mSelectorOptions = SelectorOptions.getInstance();
    }

    @Override
    public void load(@NonNull final ContentResolver cr, final int page, final String id,
                     @NonNull final IMediaTaskCallback<ImageMediaEntity> callback) {
        buildThumbnail(cr);
        buildAlbumList(cr, id, page, callback);
    }

    /**
     * 构造缩略图
     *
     * @param cr ContentResolver
     */
    private void buildThumbnail(ContentResolver cr) {
        Cursor cur = null;
        try {
            cur = Images.Thumbnails.queryMiniThumbnails(cr, QUERY_URI_THUMBNAIL,
                    Images.Thumbnails.MINI_KIND, QUERY_PROJECTION_THUMBNAIL);
            if (cur != null && cur.moveToFirst()) {
                do {
                    Long imageId = cur.getLong(cur.getColumnIndex(QUERY_PROJECTION_THUMBNAIL[0]));
                    // Uri
                    Uri imageUri = ContentUris.withAppendedId(QUERY_URI_THUMBNAIL, imageId);
                    mThumbnailMap.put(imageId, imageUri);
                } while (!cur.isLast() && cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    /**
     * 构造相册图片列表
     *
     * @param cr       ContentResolver
     * @param bucketId 相册Id
     * @param page     当前页数
     * @param callback 多媒体加载回调
     * @return 相册图片列表
     */
    private List<ImageMediaEntity> buildAlbumList(ContentResolver cr, String bucketId, int page,
                                                  @NonNull final IMediaTaskCallback<ImageMediaEntity> callback) {
        List<ImageMediaEntity> result = new ArrayList<>();
        String columns[] = getColumns();
        Cursor cursor = null;
        try {
            boolean isDefaultAlbum = TextUtils.isEmpty(bucketId);
            boolean isNeedPaging = mSelectorOptions == null || mSelectorOptions.isNeedPaging();
            boolean isNeedGif = mSelectorOptions != null && mSelectorOptions.isNeedGif();
            int totalCount = getTotalCount(cr, bucketId, columns, isDefaultAlbum, isNeedGif);

            String imageMimeType = isNeedGif ? SELECTION_IMAGE_MIME_TYPE :
                    SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF;
            String[] args = isNeedGif ? SELECTION_ARGS_IMAGE_MIME_TYPE :
                    SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF;
            String order = isNeedPaging ? Images.Media.DATE_MODIFIED + DESC + " LIMIT "
                    + page * MediaSelector.PAGE_LIMIT + " , " + MediaSelector.PAGE_LIMIT :
                    Images.Media.DATE_MODIFIED + DESC;
            String selectionId = isNeedGif ? SELECTION_ID : SELECTION_ID_WITHOUT_GIF;
            cursor = query(cr, bucketId, columns, isDefaultAlbum, isNeedGif, imageMimeType, args,
                    order, selectionId);
            addItem(totalCount, result, cursor, callback);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private void addItem(final int allCount, final List<ImageMediaEntity> result, Cursor cursor,
                         @NonNull final IMediaTaskCallback<ImageMediaEntity> callback) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Uri
                long picId = cursor.getLong(cursor.getColumnIndex(Images.Media._ID));
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , picId);

                String id = cursor.getString(cursor.getColumnIndex(Images.Media._ID));
                String size = cursor.getString(cursor.getColumnIndex(Images.Media.SIZE));
                String mimeType =
                        cursor.getString(cursor.getColumnIndex(Images.Media.MIME_TYPE));
                int width = 0;
                int height = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    width = cursor.getInt(cursor.getColumnIndex(Images.Media.WIDTH));
                    height = cursor.getInt(cursor.getColumnIndex(Images.Media.HEIGHT));
                }
                ImageMediaEntity imageItem = ImageMediaEntity.of()
                        .setId(id)
                        .setUri(uri)
                        .setThumbnailUri(mThumbnailMap.get(id))
                        .setSize(size).setMimeType(mimeType).setHeight(height).setWidth(width);
                if (!result.contains(imageItem)) {
                    result.add(imageItem);
                }
            } while (!cursor.isLast() && cursor.moveToNext());
            postMedias(result, allCount, callback);
        } else {
            postMedias(result, 0, callback);
        }
        clear();
    }

    private void postMedias(final List<ImageMediaEntity> result, final int count,
                            @NonNull final IMediaTaskCallback<ImageMediaEntity> callback) {
        LoadExecutorUtils.getInstance().runUI(new Runnable() {
            @Override
            public void run() {
                callback.postMedia(result, count);
            }
        });
    }

    private Cursor query(ContentResolver cr, String bucketId, String[] columns,
                         boolean isDefaultAlbum,
                         boolean isNeedGif, String imageMimeType, String[] args, String order,
                         String selectionId) {
        Cursor resultCursor;
        if (isDefaultAlbum) {
            resultCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, imageMimeType,
                    args, order);
        } else {
            if (isNeedGif) {
                resultCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, selectionId,
                        new String[]{bucketId, args[0], args[1], args[2]}, order);
            } else {
                resultCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, selectionId,
                        new String[]{bucketId, args[0], args[1]}, order);
            }
        }
        return resultCursor;
    }

    @NonNull
    private String[] getColumns() {
        String[] columns;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            columns = new String[]{Images.Media._ID, Images.Media.SIZE,
                    Images.Media.MIME_TYPE, Images.Media.WIDTH, Images.Media.HEIGHT};
        } else {
            columns = new String[]{Images.Media._ID, Images.Media.SIZE,
                    Images.Media.MIME_TYPE};
        }
        return columns;
    }

    private int getTotalCount(ContentResolver cr, String bucketId, String[] columns,
                              boolean isDefaultAlbum, boolean isNeedGif) {
        Cursor allCursor = null;
        int result = 0;
        try {
            if (isDefaultAlbum) {
                allCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns,
                        SELECTION_IMAGE_MIME_TYPE, SELECTION_ARGS_IMAGE_MIME_TYPE,
                        Images.Media.DATE_MODIFIED + DESC);
            } else {
                if (isNeedGif) {
                    allCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, SELECTION_ID,
                            new String[]{bucketId, MimeTypeConstants.IMAGE_JPEG,
                                    MimeTypeConstants.IMAGE_PNG, MimeTypeConstants.IMAGE_JPG,
                                    MimeTypeConstants.IMAGE_GIF},
                            Images.Media.DATE_MODIFIED + DESC);
                } else {
                    allCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns,
                            SELECTION_ID_WITHOUT_GIF,
                            new String[]{bucketId, MimeTypeConstants.IMAGE_JPEG,
                                    MimeTypeConstants.IMAGE_PNG, MimeTypeConstants.IMAGE_JPG},
                            Images.Media.DATE_MODIFIED + DESC);
                }
            }
            if (allCursor != null) {
                result = allCursor.getCount();
            }
        } finally {
            if (allCursor != null) {
                allCursor.close();
            }
        }
        return result;
    }

    private void clear() {
        if (mThumbnailMap != null) {
            mThumbnailMap.clear();
        }
    }

}
