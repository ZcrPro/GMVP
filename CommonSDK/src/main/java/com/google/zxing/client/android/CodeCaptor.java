package com.google.zxing.client.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.client.android.camera.CameraManager;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class CodeCaptor implements SurfaceHolder.Callback {
    private static final String TAG = CodeCaptor.class.getSimpleName();
    private SoftReference<Activity> mActivity;
    private boolean hasSurface;

    private Map<DecodeHintType, Object> decodeHints;
    private int width = -1, height = -1;
    private int cameraId = -1;
    private CameraManager mCameraManager;
    private SurfaceHolder mSurfaceHolder;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;

    private CaptureStatusCallback mCaptureStatusCallback;
    private CaptureResultCallback mCaptureResultCallback;
    private CaptureMainHandler mCaptureMainHandler;
    private int mAutoRestartTime;

    private CodeCaptor(Builder builder) {
        this.mActivity = new SoftReference<>(builder.mActivity);
        this.mSurfaceHolder = builder.mSurfaceHolder;
        decodeHints = new EnumMap<>(DecodeHintType.class);
        if (builder.decodeHints != null)
            decodeHints.putAll(builder.decodeHints);
        width = builder.width;
        height = builder.height;
        cameraId = builder.cameraId;

        mCaptureResultCallback = builder.mCaptureResultCallback;
        mCaptureStatusCallback = builder.mCaptureStatusCallback;
        mAutoRestartTime = builder.autoRestartTime;
        beepManager = new BeepManager(mActivity.get());
        ambientLightManager = new AmbientLightManager(mActivity.get().getApplicationContext());
        mCameraManager = new CameraManager(mActivity.get().getApplicationContext());

        // Creating the mCaptureMainHandler starts the preview, which can also throw a RuntimeException.
        mCaptureMainHandler = new CaptureMainHandler(CodeCaptor.this,decodeHints);
    }

    /**
     * 获取自动开始时间间隔
     *
     * @return
     */
    protected int getAutoRestartTime() {
        return mAutoRestartTime;
    }

    /**
     * 设置是否开启闪光灯
     *
     * @param enable
     */
    public void setTorch(boolean enable) {
        if (mCameraManager != null) {
            mCameraManager.setTorch(enable);
        }
    }

    /**
     * 主动开始下一次扫描
     */
    public void restartPreviewAndDecode() {
        if (mCaptureMainHandler != null)
            mCaptureMainHandler.restartPreviewAndDecode();
    }

    /**
     * 获取相机管理器
     *
     * @return
     */
    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    /**
     * @return
     */
    protected Handler getMainHandler() {
        return mCaptureMainHandler;
    }

    /**
     * 该方法应该在{@link Activity#onResume()}被调用
     */
    public void resume() {
//        mCaptureMainHandler = null;
        mActivity.get().setRequestedOrientation(getCurrentOrientation());

        beepManager.updatePrefs();
        ambientLightManager.start(mCameraManager);

//        inactivityTimer.resume();

        if (width > 0 && height > 0) {
            mCameraManager.setManualFramingRect(width, height);
        }
        if (cameraId >= 0) {
            mCameraManager.setManualCameraId(cameraId);
        }
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            mCaptureMainHandler.sendEmptyMessage(CaptureMainHandler.msg_initial_camera);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            mSurfaceHolder.addCallback(CodeCaptor.this);
        }

    }

    public void setHasSurface(boolean hasSurface) {
        this.hasSurface = hasSurface;
    }

    /**
     * 该方法应该在{@link Activity#onPause()} 被调用
     */
    public void pause() {
        mCaptureMainHandler.pause();
        ambientLightManager.stop();
        beepManager.close();
        mCameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            mSurfaceHolder.removeCallback(this);
        }
    }

    public void destroy() {
        if (mCaptureMainHandler != null) {
            mCaptureMainHandler.quitSynchronously();
            mCaptureMainHandler = null;
        }
        beepManager.dispose();
        ambientLightManager.dispose();
        this.mActivity = null;
        mSurfaceHolder.removeCallback(this);
        decodeHints.clear();
        mCaptureResultCallback = null;
        mCaptureStatusCallback = null;
    }

    /**
     * 获取当前屏幕选择角度
     *
     * @return
     */
    private int getCurrentOrientation() {
        int rotation = mActivity.get().getWindowManager().getDefaultDisplay().getRotation();
        if (mActivity.get().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                case Surface.ROTATION_270:
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_180:
                case Surface.ROTATION_90:
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
    }

    /**
     * 初始化相机
     */
    boolean initCameraInternal() {
        if (mSurfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCameraInternal() while already open -- late SurfaceView callback?");
            return true;
        }
        try {
            mCameraManager.openDriver(mSurfaceHolder);
            if(mCaptureStatusCallback!=null)
                mCaptureStatusCallback.onInitial(this);
            return true;
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            if (mCaptureStatusCallback != null) {
                mCaptureStatusCallback.onError(ioe);
            }
            return false;
        } catch (RuntimeException e) {
            if (mCaptureStatusCallback != null) {
                mCaptureStatusCallback.onError(e);
            }
            Log.w(TAG, "Unexpected error initializing camera", e);
            return false;
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        Log.e(TAG, "surfaceCreated " + hasSurface);
        if (!hasSurface) {
            hasSurface = true;
            mCaptureMainHandler.sendEmptyMessage(CaptureMainHandler.msg_initial_camera);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated " + hasSurface);
        hasSurface = false;
    }

    /**
     * 创建器
     */
    public static class Builder {
        private Activity mActivity;
        private SurfaceHolder mSurfaceHolder;
        private Collection<BarcodeFormat> decodeFormats;
        private Map<DecodeHintType, Object> decodeHints;
        private int width = -1, height = -1;
        private int cameraId = -1;
        private ResultPointCallback mResultPointCallback;
        private CaptureResultCallback mCaptureResultCallback;
        private int autoRestartTime = 500;//自动开始下一次扫描时间间隔
        private CaptureStatusCallback mCaptureStatusCallback;
        private String characterSet = null;

        public Builder(Activity activity) {
            mActivity = activity;
            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
            decodeHints = new HashMap<>();
        }

        public Builder characterSet(String characterSet) {
            this.characterSet = characterSet;
            return this;
        }

        public Builder put(DecodeHintType hintType, Object value) {
            if (hintType == DecodeHintType.CHARACTER_SET || hintType == DecodeHintType.NEED_RESULT_POINT_CALLBACK ||
                    hintType == DecodeHintType.POSSIBLE_FORMATS) {
                return this; // This hint is specified in another way
            }
            if (hintType.getValueType() != Void.class && hintType.getValueType().isInstance(value)) {
                decodeHints.put(hintType, value);
            } else if (hintType.getValueType() == Void.class) {
                decodeHints.put(hintType, Boolean.TRUE);
            }
            return this;
        }

        /**
         * 设置扫描完毕后 如果{@link CaptureResultCallback#onHandleDecodeResult(Result)}返回true的情况下
         * 自动开始下一次扫描的时间间隔
         *
         * @param autoRestartTime
         * @return
         */
        public Builder autoRestartTime(int autoRestartTime) {
            this.autoRestartTime = autoRestartTime;
            return this;
        }

        /**
         * 设置扫码结果回调接口
         *
         * @param captureResultCallback
         * @return
         */
        public Builder resultCallback(CaptureResultCallback captureResultCallback) {
            mCaptureResultCallback = captureResultCallback;
            return this;
        }

        /**
         * 设置扫码过程中有可能有结果
         *
         * @param resultPointCallback
         * @return
         */
        public Builder resultPointCallback(ResultPointCallback resultPointCallback) {
            mResultPointCallback = resultPointCallback;
            return this;
        }

        /**
         * 设置扫码错误回调
         *
         * @param captureStatusCallback
         */
        public Builder exceptionCallback(CaptureStatusCallback captureStatusCallback) {
            mCaptureStatusCallback = captureStatusCallback;
            return this;
        }

        /**
         * 设置surfaceView
         *
         * @param surfaceHolder
         * @return
         */
        public Builder surfaceHolder(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
            return this;
        }

        /**
         * 设置支持解析格式
         *
         * @param formats
         */
        public Builder decodeFormats(BarcodeFormat... formats) {
            if (formats == null || formats.length == 0) {
                return this;
            }
            decodeFormats.clear();
            decodeFormats.addAll(Arrays.asList(formats));
            return this;
        }

        /**
         * Allows third party apps to specify the scanning rectangle dimensions, rather than
         * determine
         * them automatically based on screen resolution.
         * 允许第三方应用程序指定扫描矩形尺寸，而不是根据屏幕分辨率自动确定尺寸。
         *
         * @param width  The width in pixels to scan.
         * @param height The height in pixels to scan.
         */
        public Builder framingRect(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * 设置摄像头ID
         *
         * @param cameraId
         */
        public Builder cameraId(int cameraId) {
            this.cameraId = cameraId;
            return this;
        }

        /**
         * 创建一个{@link CodeCaptor}实体对象
         *
         * @return
         */
        public CodeCaptor create() {

            if (mActivity == null) {
                throw new NullPointerException("The Activity reference is null");
            }
            if (mSurfaceHolder == null) {
                throw new IllegalArgumentException("The surfaceHolder has not been set.");
            }
            if (autoRestartTime < 0)
                autoRestartTime = 0;

            if (decodeFormats.size() == 0) {
                decodeFormats.add(BarcodeFormat.QR_CODE);//默认支持QR_CODE
            }
            decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
            if (characterSet != null) {
                decodeHints.put(DecodeHintType.CHARACTER_SET, characterSet);
            }
            if (mResultPointCallback != null)
                decodeHints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, mResultPointCallback);

            return new CodeCaptor(this);
        }
    }


    protected boolean handleDecode(Result result) {
        beepManager.playBeepSoundAndVibrate();
        if (mCaptureResultCallback != null) {
            return mCaptureResultCallback.onHandleDecodeResult(result);
        }
        return false;
    }


    public interface CaptureResultCallback {
        boolean onHandleDecodeResult(Result rawResult);
    }

    public interface CaptureStatusCallback {
        void onInitial(CodeCaptor codeCaptor);
        void onError(Exception e);
    }
}
