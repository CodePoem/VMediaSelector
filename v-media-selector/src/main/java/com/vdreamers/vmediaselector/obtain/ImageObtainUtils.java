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
public class ImageObtainUtils {

    private static final String TAG = ImageObtainUtils.class.getSimpleName();

    /**
     * 获取回调
     */
    private ObtainListener mObtainListener;

    private ImageObtainUtils() {
    }

    public static ImageObtainUtils of() {
        return new ImageObtainUtils();
    }

    public ImageObtainUtils setCallback(ObtainListener obtainListener) {
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

                final List<String> obtainFilePathList = new ArrayList<>();
                if (context == null || fileUriList == null) {
                    LoadExecutorUtils.getInstance().runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (mObtainListener != null) {
                                mObtainListener.onSuccess(obtainFilePathList);
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
                        obtainFilePathList.add(file.getAbsolutePath());
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
                            mObtainListener.onSuccess(obtainFilePathList);
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
