package com.king.zxing;

import android.content.Intent;
import android.view.View;

import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;
import com.king.zxing.analyze.Analyzer;
import com.king.zxing.analyze.AreaRectAnalyzer;
import com.king.zxing.analyze.BarcodeFormatAnalyzer;
import com.king.zxing.analyze.ImageAnalyzer;
import com.king.zxing.analyze.MultiFormatAnalyzer;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class CameraScan implements ICamera,ICameraControl {

    public static String SCAN_RESULT = "SCAN_RESULT";

    /** A camera on the device facing the same direction as the device's screen. */
    public static int LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT;
    /** A camera on the device facing the opposite direction as the device's screen. */
    public static int LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK;


    /**
     * 是否需要支持自动缩放
     */
    private boolean isNeedAutoZoom = false;

    /**
     * 是否需要支持触摸缩放
     */
    private boolean isNeedTouchZoom = true;

    /**
     * 是否需要支持触摸缩放
     * @return
     */
    protected boolean isNeedTouchZoom() {
        return isNeedTouchZoom;
    }


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

    /**
     * 设置相机配置，请在{@link #startCamera()}之前调用
     * @param cameraConfig
     */
    public abstract CameraScan setCameraConfig(CameraConfig cameraConfig);

    /**
     * 设置是否分析图像，通过此方法可以动态控制是否分析图像，常用于中断扫码识别。如：连扫时，扫到结果，然后停止分析图像
     * @param analyze
     */
    public abstract CameraScan setAnalyzeImage(boolean analyze);

    /**
     * 设置分析器，内置了一些{@link Analyzer}的实现类如下
     * @see {@link MultiFormatAnalyzer}
     * @see {@link AreaRectAnalyzer}
     * @see {@link ImageAnalyzer}
     *
     * @see {@link BarcodeFormatAnalyzer}
     * @see {@link QRCodeReader}
     *
     * @param analyzer
     */
    public abstract CameraScan setAnalyzer(Analyzer analyzer);

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

    /**
     * 设置光线足够暗的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
     * @param lightLux
     */
    public abstract CameraScan setDarkLightLux(float lightLux);

    /**
     * 设置光线足够明亮的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
     * @param lightLux
     */
    public abstract CameraScan setBrightLightLux(float lightLux);

    public interface OnScanResultCallback{
        /**
         * 扫码结果回调
         * @param result
         * @return 返回false表示不拦截，将关闭扫码界面并将结果返回给调用界面；
         *  返回true表示拦截，需自己处理逻辑。当isAnalyze为true时，默认会继续分析图像（也就是连扫）。
         *  如果只是想拦截扫码结果回调，并不想继续分析图像（不想连扫），请在拦截扫码逻辑处通过调
         *  用{@link CameraScan#setAnalyzeImage(boolean)}，
         *  因为{@link CameraScan#setAnalyzeImage(boolean)}方法能动态控制是否继续分析图像。
         *
         */
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
