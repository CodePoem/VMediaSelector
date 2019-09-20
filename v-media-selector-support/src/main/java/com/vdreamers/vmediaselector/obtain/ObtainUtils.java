package com.vdreamers.vmediaselector.obtain;

import android.content.Context;
import android.net.Uri;

import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;
import com.vdreamers.vmediaselector.core.utils.MediaSelectorLogUtils;
import com.vdreamers.vmediaselector.core.utils.UriFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 图片获取工具类
 * <p>
 * date 2019/09/19 14:12:35
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class ObtainUtils {

    private static final String TAG = ObtainUtils.class.getSimpleName();

    /**
     * 获取回调
     */
    private ObtainListener mObtainListener;

    private ObtainUtils() {
    }

    public static ObtainUtils of() {
        return new ObtainUtils();
    }

    public ObtainUtils setCallback(ObtainListener obtainListener) {
        mObtainListener = obtainListener;
        return this;
    }

    public boolean obtain(final Context context, final List<Uri> fileUriList) {
        FutureTask<Boolean> task =
                LoadExecutorUtils.getInstance().runWorker(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                MediaSelectorLogUtils.d(TAG, "------------------ start obtain file " +
                        "------------------");
                LoadExecutorUtils.getInstance().runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (mObtainListener != null) {
                            mObtainListener.onStart();
                        }
                    }
                });

                final List<File> obtainFiles = new ArrayList<>();
                if (context == null || fileUriList == null) {
                    LoadExecutorUtils.getInstance().runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (mObtainListener != null) {
                                mObtainListener.onSuccess(obtainFiles);
                            }
                        }
                    });
                    return true;
                }

                try {
                    for (Uri uri : fileUriList) {
                        if (uri == null) {
                            continue;
                        }
                        File file = UriFileUtils.getFileFromUri(context, uri);
                        if (file == null) {
                            continue;
                        }
                        obtainFiles.add(file);
                    }
                } catch (OutOfMemoryError | NullPointerException | IllegalArgumentException e) {
                    LoadExecutorUtils.getInstance().runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (mObtainListener != null) {
                                mObtainListener.onFailed(e);
                            }
                        }
                    });
                    MediaSelectorLogUtils.d("image obtain fail!");
                    return false;
                }

                LoadExecutorUtils.getInstance().runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (mObtainListener != null) {
                            mObtainListener.onSuccess(obtainFiles);
                        }
                    }
                });
                MediaSelectorLogUtils.i(TAG, "------------------ end obtain file " +
                        "------------------");
                return true;
            }
        });
        try {
            return task != null && task.get();
        } catch (InterruptedException | ExecutionException ignore) {
            return false;
        }
    }
}
