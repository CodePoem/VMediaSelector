package com.vdreamers.vmediaselector.core.impl.task.impl;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.vdreamers.vmediaselector.core.entity.VideoMediaEntity;
import com.vdreamers.vmediaselector.core.impl.callback.IMediaTaskCallback;
import com.vdreamers.vmediaselector.core.impl.task.IMediaTask;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;
import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频加载任务
 * <p>
 * date 2019-09-18 20:31:59
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@WorkerThread
public class VideoTask implements IMediaTask<VideoMediaEntity> {

    /**
     * 视频查询Uri
     */
    private static final Uri QUERY_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    /**
     * 视频查询列
     */
    private static final String[] QUERY_PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DURATION
    };

    @Override
    public void load(final ContentResolver cr, final int page, String id,
                     final IMediaTaskCallback<VideoMediaEntity> callback) {
        loadVideos(cr, page, callback);
    }

    private void loadVideos(ContentResolver cr, int page,
                            @NonNull final IMediaTaskCallback<VideoMediaEntity> callback) {
        final List<VideoMediaEntity> videoMedias = new ArrayList<>();
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC" +
                " LIMIT " + page * MediaSelector.PAGE_LIMIT + " , " + MediaSelector.PAGE_LIMIT;
        final Cursor cursor = cr.query(QUERY_URI, QUERY_PROJECTION, null, null, sortOrder);
        try {
            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getCount();
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    // Uri
                    Uri uri = ContentUris.withAppendedId(QUERY_URI, id);
                    String title =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    String type =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    String size =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String date =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
                    String duration =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    VideoMediaEntity video = VideoMediaEntity.of()
                            .setId(id)
                            .setUri(uri)
                            .setTitle(title)
                            .setDuration(duration)
                            .setSize(size)
                            .setDateTaken(date)
                            .setMimeType(type);
                    videoMedias.add(video);
                } while (!cursor.isLast() && cursor.moveToNext());
                postMedias(callback, videoMedias, count);
            } else {
                postMedias(callback, videoMedias, 0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void postMedias(@NonNull final IMediaTaskCallback<VideoMediaEntity> callback,
                            final List<VideoMediaEntity> videoMedias, final int count) {
        LoadExecutorUtils.getInstance().runUI(new Runnable() {
            @Override
            public void run() {
                callback.postMedia(videoMedias, count);
            }
        });
    }

}
