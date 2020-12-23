package com.king.zxing;


import android.view.MotionEvent;

import com.google.zxing.Result;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface ICameraScan {

    /**
     * 启动相机预览
     */
    void startCamera();

    /**
     * 停止相机预览
     */
    void stopCamera();


    /**
     * 放大
     */
    void zoomIn();

    /**
     * 缩小
     */
    void zoomOut();

    /**
     * 缩放到指定比例
     * @param ratio
     */
    void zoomTo(float ratio);

    /**
     * 放大
     */
    void lineZoomIn();

    /**
     * 缩小
     */
    void lineZoomOut();

    /**
     * 线性缩放到指定比例
     * @param linearZoom
     */
    void lineZoomTo(@FloatRange(from = 0.0,to = 1.0) float linearZoom);


    /**
     * 获取{@link Camera}
     * @return
     */
    @Nullable Camera getCamera();

    void release();

}
