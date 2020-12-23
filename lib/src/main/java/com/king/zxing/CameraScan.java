package com.king.zxing;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import com.google.zxing.Result;
import com.king.zxing.analyze.Analyzer;
import com.king.zxing.util.LogUtils;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;

import static com.king.zxing.CameraScan.*;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class CameraScan implements ICameraScan {

    /**
     * 默认触控误差值
     */
    private static final int DEVIATION = 6;

    public static String SCAN_RESULT = "SCAN_RESULT";

    /** A camera on the device facing the same direction as the device's screen. */
    public static int LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT;
    /** A camera on the device facing the opposite direction as the device's screen. */
    public static int LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK;


    /**
     * 是否需要支持自动缩放
     */
    private boolean isNeedAutoZoom = true;

    /**
     * 是否需要支持触摸缩放
     */
    private boolean isNeedTouchZoom = true;

    private float mOldDistance;


    /**
     * 设置是否需要支持触摸缩放
     * @param needTouchZoom
     * @return
     */
    public CameraScan setNeedTouchZoom(boolean needTouchZoom) {
        isNeedTouchZoom = needTouchZoom;
        return this;
    }

    /**
     * 是否需要支持自动缩放
     * @return
     */
    protected boolean isNeedAutoZoom() {
        return isNeedAutoZoom;
    }

    /**
     * 设置是否需要支持自动缩放
     * @param needAutoZoom
     * @return
     */
    public CameraScan setNeedAutoZoom(boolean needAutoZoom) {
        isNeedAutoZoom = needAutoZoom;
        return this;
    }

    protected boolean onTouchEvent(MotionEvent event) {
        if(getCamera() != null && isNeedTouchZoom){
            LogUtils.d("action:" + (event.getAction() & MotionEvent.ACTION_MASK));
            if(event.getPointerCount() > 1) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {//多点触控
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mOldDistance = calcFingerSpacing(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newDistance = calcFingerSpacing(event);

                        if (newDistance > mOldDistance + DEVIATION) {
                            zoomIn();
                        } else if (newDistance < mOldDistance - DEVIATION) {
                            zoomOut();
                        }
                        mOldDistance = newDistance;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP:
                        return false;
                }

            }
            return true;
        }
        return false;
    }

    /**
     * 计算两指间距离
     * @param event
     * @return
     */
    private float calcFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 设置相机配置，请在{@link #startCamera()}之前调用
     * @param cameraConfig
     */
    public abstract CameraScan setCameraConfig(CameraConfig cameraConfig);

    /**
     * 设置是否分析图像
     * @param analyze
     */
    public abstract CameraScan setAnalyzeImage(boolean analyze);

    /**
     * 设置分析器
     * @param analyzer
     */
    public abstract CameraScan setAnalyzer(Analyzer analyzer);

    /**
     * 设置手电筒是否开启
     * @param torch
     */
    public abstract CameraScan enableTorch(boolean torch);

    /**
     * 手电筒是否开启
     * @return
     */
    public abstract boolean isTorchEnabled();

    /**
     * 是否支持闪光灯
     * @return
     */
    public abstract boolean hasFlashUnit();

    /**
     * 设置是否震动
     * @param vibrate
     */
    public abstract CameraScan setVibrate(boolean vibrate);

    /**
     * 设置是否播放提示音
     * @param playBeep
     */
    public abstract CameraScan setPlayBeep(boolean playBeep);

    /**
     * 设置扫码结果回调
     * @param callback
     */
    public abstract CameraScan setOnScanResultCallback(OnScanResultCallback callback);

    /**
     * 绑定手电筒，绑定后可根据光线传感器，动态显示或隐藏手电筒
     * @param v
     */
    public abstract CameraScan bindFlashlightView(@Nullable View v);


    public interface OnScanResultCallback{
        boolean onScanResultCallback(Result result);
    }


    @Nullable
    public static String parseScanResult(Intent data){
        if(data != null){
            return data.getStringExtra(SCAN_RESULT);
        }
        return null;
    }

}
