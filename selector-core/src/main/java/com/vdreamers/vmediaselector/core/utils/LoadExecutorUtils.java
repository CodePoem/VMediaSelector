package com.vdreamers.vmediaselector.core.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 加载线程池工具类
 * <p>
 * date 2019-09-18 20:25:47
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class LoadExecutorUtils {

    private ExecutorService mExecutorService;

    private LoadExecutorUtils() {
    }

    private static class SingletonInstance {
        private static final LoadExecutorUtils INSTANCE = new LoadExecutorUtils();
    }

    public static LoadExecutorUtils getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void runWorker(@NonNull Runnable runnable) {
        ensureWorkerHandlerNotNull();
        try {
            mExecutorService.execute(runnable);
        } catch (Exception e) {
            MediaSelectorLogUtils.d("runnable stop running unexpected. " + e.getMessage());
        }
    }

    @Nullable
    public FutureTask<Boolean> runWorker(@NonNull Callable<Boolean> callable) {
        ensureWorkerHandlerNotNull();
        FutureTask<Boolean> task = null;
        try {
            task = new FutureTask<>(callable);
            mExecutorService.submit(task);
            return task;
        } catch (Exception e) {
            MediaSelectorLogUtils.d("callable stop running unexpected. " + e.getMessage());
        }
        return task;
    }

    public void runUI(@NonNull Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        Handler handler = ensureUiHandlerNotNull();
        try {
            handler.post(runnable);
        } catch (Exception e) {
            MediaSelectorLogUtils.d("update UI task fail. " + e.getMessage());
        }
    }

    private void ensureWorkerHandlerNotNull() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
    }

    private Handler ensureUiHandlerNotNull() {
        return new Handler(Looper.getMainLooper());
    }
}
