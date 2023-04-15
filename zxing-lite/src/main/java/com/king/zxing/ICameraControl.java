package com.king.zxing;

import androidx.annotation.FloatRange;

/**
 * 相机控制：主要包括调节焦距和闪光灯控制
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface ICameraControl {

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
     *
     * @param ratio 缩放比例
     */
    void zoomTo(float ratio);

    /**
     * 线性放大
     */
    void lineZoomIn();

    /**
     * 线性缩小
     */
    void lineZoomOut();

    /**
     * 线性缩放到指定比例
     *
     * @param linearZoom 线性缩放比例；范围在：0.0 ~ 1.0之间
     */
    void lineZoomTo(@FloatRange(from = 0.0, to = 1.0) float linearZoom);

    /**
     * 设置闪光灯（手电筒）是否开启
     *
     * @param torch 是否开启闪光灯（手电筒）
     */
    void enableTorch(boolean torch);

    /**
     * 闪光灯（手电筒）是否开启
     *
     * @return 闪光灯（手电筒）是否开启
     */
    boolean isTorchEnabled();

    /**
     * 是否支持闪光灯
     *
     * @return 是否支持闪光灯
     */
    boolean hasFlashUnit();
}
