package com.vdreamers.vmediaselector.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.utils.LoadExecutorUtils;
import com.vdreamers.vmediaselector.core.utils.MediaSelectorLogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 相机辅助类
 * used by {@link BaseMediaViewFragment}
 * <p>
 * date 2019-09-18 22:00:51
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings("WeakerAccess")
public class CameraPickerHelper {
    private static final int MAX_CAMER_PHOTO_SIZE = 4 * 1024 * 1024;
    public static final int REQ_CODE_CAMERA = 0x2001;
    private static final String STATE_SAVED_KEY = "state_saved_key";

    private File mOutputFile;
    private Callback mCallback;

    public interface Callback {
        /**
         * 成功回调
         *
         * @param helper
         */
        void onFinish(@NonNull CameraPickerHelper helper);

        /**
         * 错误回调
         *
         * @param helper
         */
        void onError(@NonNull CameraPickerHelper helper);
    }

    public CameraPickerHelper(@Nullable Bundle savedInstance) {
        if (savedInstance != null) {
            SavedState state = savedInstance.getParcelable(STATE_SAVED_KEY);
            if (state != null) {
                mOutputFile = state.mOutputFile;
            }
        }
    }

    public void setPickCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void onSaveInstanceState(Bundle out) {
        SavedState state = new SavedState();
        state.mOutputFile = mOutputFile;
        out.putParcelable(STATE_SAVED_KEY, state);
    }

    /**
     * start system camera to take a picture
     *
     * @param activity      not null if fragment is null.
     * @param fragment      not null if activity is null.
     * @param subFolderPath a folder in external DCIM,must start with "/".
     */
    public void startCamera(final Activity activity, final Fragment fragment,
                            final String subFolderPath) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || !takePhotoSecure(activity, fragment,
                subFolderPath)) {
            FutureTask<Boolean> task =
                    LoadExecutorUtils.getInstance().runWorker(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            try {
                                // try...try...try
                                Camera camera = Camera.open();
                                camera.release();
                            } catch (Exception e) {
                                MediaSelectorLogUtils.d("camera is not available.");
                                return false;
                            }
                            return true;
                        }
                    });
            try {
                if (task != null && task.get()) {
                    startCameraIntent(activity, fragment, subFolderPath,
                            MediaStore.ACTION_IMAGE_CAPTURE, REQ_CODE_CAMERA);
                } else {
                    callbackError();
                }
            } catch (InterruptedException | ExecutionException ignore) {
                callbackError();
            }

        }
    }

    private boolean takePhotoSecure(Activity activity, Fragment fragment, String subDir) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                startCameraIntent(activity, fragment, subDir, MediaStore.ACTION_IMAGE_CAPTURE,
                        REQ_CODE_CAMERA);
                return true;
            } catch (ActivityNotFoundException ignore) {
                return false;
            }
        }
        return false;
    }

    private void callbackFinish() {
        if (mCallback != null) {
            mCallback.onFinish(CameraPickerHelper.this);
        }
    }

    private void callbackError() {
        if (mCallback != null) {
            mCallback.onError(CameraPickerHelper.this);
        }
    }

    private void startActivityForResult(Activity activity, Fragment fragment, final Intent intent
            , final int reqCodeCamera) throws ActivityNotFoundException {
        if (fragment == null) {
            activity.startActivityForResult(intent, reqCodeCamera);
        } else {
            fragment.startActivityForResult(intent, reqCodeCamera);
        }
    }

    private void startCameraIntent(final Activity activity, final Fragment fragment,
                                   String subFolder,
                                   final String action, final int requestCode) {
        String cameraOutDir;
        if (SelectorOptions.getInstance().isStoreCameraImage()) {
            cameraOutDir = MediaFileHelper.getExternalDCIM(subFolder);
        } else {
            cameraOutDir = MediaFileHelper.getCacheDir(activity, subFolder);
        }
        if (MediaFileHelper.createFile(cameraOutDir)) {
            mOutputFile = new File(cameraOutDir, System.currentTimeMillis() + ".jpg");
            Intent intent = new Intent(action);
            Uri outputUri = FileProviderUtils.getFileUri(activity.getApplicationContext(),
                    mOutputFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            try {
                startActivityForResult(activity, fragment, intent, requestCode);
            } catch (ActivityNotFoundException ignore) {
                callbackError();
            }
        }
    }

    public File getOutputFile() {
        return mOutputFile;
    }

    /**
     * deal with the system camera's shot.
     */
    public boolean onActivityResult(final int requestCode, final int resultCode) {
        if (requestCode != REQ_CODE_CAMERA) {
            return false;
        }
        if (resultCode != Activity.RESULT_OK) {
            callbackError();
            return false;
        }
        FutureTask<Boolean> task =
                LoadExecutorUtils.getInstance().runWorker(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return rotateImage(resultCode);
                    }
                });
        try {
            if (task != null && task.get()) {
                callbackFinish();
            } else {
                callbackError();
            }
        } catch (InterruptedException | ExecutionException ignore) {
            callbackError();
        }
        return true;
    }

    private boolean rotateSourceFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            return false;
        }
        FileOutputStream outputStream = null;
        Bitmap bitmap = null;
        Bitmap outBitmap = null;
        try {
            int degree = MediaExifHelper.getRotateDegree(file.getAbsolutePath());
            if (degree == 0) {
                return true;
            }
            int quality = file.length() >= MAX_CAMER_PHOTO_SIZE ? 90 : 100;
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            outBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, false);
            outputStream = new FileOutputStream(file);
            outBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            return true;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    MediaSelectorLogUtils.d("IOException when output stream closing!");
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (outBitmap != null) {
                outBitmap.recycle();
            }
        }
    }

    private boolean rotateImage(int resultCode) throws IOException {
        return resultCode == Activity.RESULT_OK && rotateSourceFile(mOutputFile);
    }

    public void release() {
        mOutputFile = null;
    }

    private static class SavedState implements Parcelable {
        private File mOutputFile;

        SavedState() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(this.mOutputFile);
        }

        protected SavedState(Parcel in) {
            this.mOutputFile = (File) in.readSerializable();
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
