package com.vdreamers.vmediaselector.core.impl.task;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.vdreamers.vmediaselector.core.impl.callback.IAlbumTaskCallback;
import com.vdreamers.vmediaselector.core.impl.callback.IMediaTaskCallback;
import com.vdreamers.vmediaselector.core.impl.task.impl.AlbumTask;
import com.vdreamers.vmediaselector.core.impl.task.impl.ImageTask;
import com.vdreamers.vmediaselector.core.impl.task.impl.VideoTask;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;

/**
 * 加载管理器
 * The Manager to load {@link IMediaTask} and {@link AlbumTask}, holding {@link SelectorOptions}.
 * <p>
 * date 2019-09-18 20:32:06
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class LoadManager {

    private LoadManager() {
    }

    private static class SingletonHolder {
        private static final LoadManager INSTANCE = new LoadManager();
    }

    public static LoadManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void loadMedia(@NonNull final ContentResolver cr, final int page,
                          final String id, @NonNull final IMediaTaskCallback callback) {
        final IMediaTask task = SelectorOptions.getInstance().isImageMode() ? new ImageTask() : new VideoTask();
        LoadExecutorUtils.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                task.load(cr, page, id, callback);
            }
        });

    }

    public void loadAlbum(@NonNull final ContentResolver cr,
                          @NonNull final IAlbumTaskCallback callback) {
        LoadExecutorUtils.getInstance().runWorker(new Runnable() {

            @Override
            public void run() {
                new AlbumTask().start(cr, callback);
            }
        });

    }

}
