package com.king.zxing;

import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.Map;

import androidx.annotation.FloatRange;


/**
 * 解码配置：主要用于在扫码识别时，提供一些配置，便于扩展。通过配置可决定内置分析器的能力，从而间接的控制并简化扫码识别的流程
 * <p></>
 * 设置解码 {@link #setHints(Map)}内置的一些解码可参见如下：
 * @see {@link DecodeFormatManager#DEFAULT_HINTS}
 * @see {@link DecodeFormatManager#ALL_HINTS}
 * @see {@link DecodeFormatManager#CODE_128_HINTS}
 * @see {@link DecodeFormatManager#QR_CODE_HINTS}
 * @see {@link DecodeFormatManager#ONE_DIMENSIONAL_HINTS}
 * @see {@link DecodeFormatManager#TWO_DIMENSIONAL_HINTS}
 * @see {@link DecodeFormatManager#DEFAULT_HINTS}
 *
 * 如果不满足您也可以通过{@link DecodeFormatManager#createDecodeHints(BarcodeFormat...)}自己配置支持的格式
 *
 * <p></>
 * 识别区域可设置的方式有如下几种：
 * {@link #setFullAreaScan(boolean)} 设置是否支持全区域扫码识别，优先级比识别区域高
 * {@link #setAnalyzeAreaRect(Rect)} 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
 * {@link #setAreaRectRatio(float)} 设置识别区域比例，默认{@link #DEFAULT_AREA_RECT_RATIO}，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
 *
 * 因为{@link androidx.camera.view.PreviewView}的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
 * 您还可以通过{@link CameraScan#setCameraConfig(CameraConfig)}去自定义配置{@link CameraConfig}的配置信息控制预览相关配置信息
 *
 * 即判定区域分析的优先级顺序为:{@link #setFullAreaScan(boolean)} -> {@link #setAnalyzeAreaRect(Rect)} -> {@link #setAreaRectRatio(float)}
 * <p></>
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DecodeConfig {

    private Map<DecodeHintType,Object> hints = DecodeFormatManager.DEFAULT_HINTS;

    public static final float DEFAULT_AREA_RECT_RATIO = 0.8f;

    /**
     * 是否支持使用多解码
     */
    private boolean isMultiDecode = true;

    /**
     * 是否支持识别反色码（条码黑白颜色反转的码）
     */
    private boolean isSupportLuminanceInvert;
    /**
     * 是否支持识别反色码（条码黑白颜色反转的码）使用多解码
     */
    private boolean isSupportLuminanceInvertMultiDecode;
    /**
     * 是否支持垂直的条码
     */
    private boolean isSupportVerticalCode;
    /**
     * 是否支持垂直的条码，使用多解码
     */
    private boolean isSupportVerticalCodeMultiDecode;

    /**
     * 需要分析识别区域
     */
    private Rect analyzeAreaRect;

    /**
     * 是否支持全区域扫码识别
     */
    private boolean isFullAreaScan = false;

    /**
     * 识别区域比例，默认0.8
     */
    private float areaRectRatio = DEFAULT_AREA_RECT_RATIO;
    /**
     * 识别区域垂直方向偏移量
     */
    private int areaRectVerticalOffset;
    /**
     * 识别区域水平方向偏移量
     */
    private int areaRectHorizontalOffset;

    public DecodeConfig(){

    }

    public Map<DecodeHintType, Object> getHints() {
        return hints;
    }

    /**
     * 设置解码
     * @param hints {@link DecodeFormatManager}
     *
     * 内置的一些解码可参见如下：
     * @see {@link DecodeFormatManager#DEFAULT_HINTS}
     * @see {@link DecodeFormatManager#ALL_HINTS}
     * @see {@link DecodeFormatManager#CODE_128_HINTS}
     * @see {@link DecodeFormatManager#QR_CODE_HINTS}
     * @see {@link DecodeFormatManager#ONE_DIMENSIONAL_HINTS}
     * @see {@link DecodeFormatManager#TWO_DIMENSIONAL_HINTS}
     * @see {@link DecodeFormatManager#DEFAULT_HINTS}
     *
     * 如果不满足您也可以通过{@link DecodeFormatManager#createDecodeHints(BarcodeFormat...)}自己配置支持的格式
     *
     * @return
     */
    public DecodeConfig setHints(Map<DecodeHintType, Object> hints) {
        this.hints = hints;
        return this;
    }

    /**
     * 是否支持识别反色码，黑白颜色反转
     * @return
     */
    public boolean isSupportLuminanceInvert() {
        return isSupportLuminanceInvert;
    }

    /**
     * 设置是否支持识别反色码，黑白颜色反转
     * @param supportLuminanceInvert 默认为{@code false}，想要增强支持扫码识别反色码时可使用，相应的也会增加性能消耗。
     * @return
     */
    public DecodeConfig setSupportLuminanceInvert(boolean supportLuminanceInvert) {
        isSupportLuminanceInvert = supportLuminanceInvert;
        return this;
    }

    /**
     * 是否支持扫垂直的条码
     * @return
     */
    public boolean isSupportVerticalCode() {
        return isSupportVerticalCode;
    }

    /**
     * 设置是否支持扫垂直的条码
     * @param supportVerticalCode 默认为{@code false}，想要增强支持扫码识别垂直的条码时可使用，相应的也会增加性能消耗。
     * @return
     */
    public DecodeConfig setSupportVerticalCode(boolean supportVerticalCode) {
        isSupportVerticalCode = supportVerticalCode;
        return this;
    }

    /**
     * 是否支持使用多解码
     * @return
     */
    public boolean isMultiDecode() {
        return isMultiDecode;
    }

    /**
     * 是否支持使用多解码
     * @see {@link HybridBinarizer} , {@link GlobalHistogramBinarizer}
     * @param multiDecode 默认为{@code true}
     * @return
     */
    public DecodeConfig setMultiDecode(boolean multiDecode) {
        isMultiDecode = multiDecode;
        return this;
    }

    /**
     *  是否支持识别反色码（条码黑白颜色反转的码）使用多解码
     * @return
     */
    public boolean isSupportLuminanceInvertMultiDecode() {
        return isSupportLuminanceInvertMultiDecode;
    }

    /**
     * 设置是否支持识别反色码（条码黑白颜色反转的码）使用多解码
     * @see {@link HybridBinarizer} , {@link GlobalHistogramBinarizer}
     * @param supportLuminanceInvertMultiDecode  默认为{@code false}，想要增强支持扫码识别反色码时可使用，相应的也会增加性能消耗。
     * @return
     */
    public DecodeConfig setSupportLuminanceInvertMultiDecode(boolean supportLuminanceInvertMultiDecode) {
        isSupportLuminanceInvertMultiDecode = supportLuminanceInvertMultiDecode;
        return this;
    }

    /**
     * 是否支持垂直的条码，使用多解码
     * @return
     */
    public boolean isSupportVerticalCodeMultiDecode() {
        return isSupportVerticalCodeMultiDecode;
    }

    /**
     * 设置是否支持垂直的条码，使用多解码
     * @see {@link HybridBinarizer} , {@link GlobalHistogramBinarizer}
     * @param supportVerticalCodeMultiDecode 默认为{@code false}，想要增强支持扫码识别垂直的条码时可使用，相应的也会增加性能消耗。
     * @return
     */
    public DecodeConfig setSupportVerticalCodeMultiDecode(boolean supportVerticalCodeMultiDecode) {
        isSupportVerticalCodeMultiDecode = supportVerticalCodeMultiDecode;
        return this;
    }

    /**
     * 需要分析识别区域
     * @return
     */
    public Rect getAnalyzeAreaRect() {
        return analyzeAreaRect;
    }

    /**
     * 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * @param analyzeAreaRect
     *
     * 识别区域可设置的方式有如下几种：
     * {@link #setFullAreaScan(boolean)} 设置是否支持全区域扫码识别，优先级比识别区域高
     * {@link #setAnalyzeAreaRect(Rect)} 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * {@link #setAreaRectRatio(float)} 设置识别区域比例，默认{@link #DEFAULT_AREA_RECT_RATIO}，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     *
     * 因为{@link androidx.camera.view.PreviewView}的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
     * 您还可以通过{@link CameraScan#setCameraConfig(CameraConfig)}去自定义配置{@link CameraConfig}的配置信息控制预览相关配置信息
     *
     * 即判定区域分析的优先级顺序为:{@link #setFullAreaScan(boolean)} -> {@link #setAnalyzeAreaRect(Rect)} -> {@link #setAreaRectRatio(float)}
     *
     * @return
     */
    public DecodeConfig setAnalyzeAreaRect(Rect analyzeAreaRect) {
        this.analyzeAreaRect = analyzeAreaRect;
        return this;
    }

    /**
     * 是否支持全区域扫码识别
     * @return
     */
    public boolean isFullAreaScan() {
        return isFullAreaScan;
    }

    /**
     * 设置是否支持全区域扫码识别，优先级比识别区域高
     * @param fullAreaScan 默认为{@code true}
     *
     * 识别区域可设置的方式有如下几种：
     * {@link #setFullAreaScan(boolean)} 设置是否支持全区域扫码识别，优先级比识别区域高
     * {@link #setAnalyzeAreaRect(Rect)} 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * {@link #setAreaRectRatio(float)} 设置识别区域比例，默认{@link #DEFAULT_AREA_RECT_RATIO}，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     *
     * 因为{@link androidx.camera.view.PreviewView}的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
     * 您还可以通过{@link CameraScan#setCameraConfig(CameraConfig)}去自定义配置{@link CameraConfig}的配置信息控制预览相关配置信息
     *
     * 即判定区域分析的优先级顺序为:{@link #setFullAreaScan(boolean)} -> {@link #setAnalyzeAreaRect(Rect)} -> {@link #setAreaRectRatio(float)}
     * @return
     */
    public DecodeConfig setFullAreaScan(boolean fullAreaScan) {
        isFullAreaScan = fullAreaScan;
        return this;
    }

    /**
     * 识别区域比例，默认{@link #DEFAULT_AREA_RECT_RATIO}，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
     * @return
     */
    public float getAreaRectRatio() {
        return areaRectRatio;
    }

    /**
     * 设置识别区域比例，默认{@link #DEFAULT_AREA_RECT_RATIO}，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     * @param areaRectRatio
     *
     * 识别区域可设置的方式有如下几种：
     * {@link #setFullAreaScan(boolean)} 设置是否支持全区域扫码识别，优先级比识别区域高
     * {@link #setAnalyzeAreaRect(Rect)} 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * {@link #setAreaRectRatio(float)} 设置识别区域比例，默认{@link #DEFAULT_AREA_RECT_RATIO}，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     *
     * 因为{@link androidx.camera.view.PreviewView}的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
     * 您还可以通过{@link CameraScan#setCameraConfig(CameraConfig)}去自定义配置{@link CameraConfig}的配置信息控制预览相关配置信息
     *
     * 即判定区域分析的优先级顺序为:{@link #setFullAreaScan(boolean)} -> {@link #setAnalyzeAreaRect(Rect)} -> {@link #setAreaRectRatio(float)}
     *
     * @return
     */
    public DecodeConfig setAreaRectRatio(@FloatRange(from = 0.5,to = 1.0) float areaRectRatio) {
        this.areaRectRatio = areaRectRatio;
        return this;
    }

    /**
     * 识别区域垂直方向偏移量，支持负数，大于0时，居中心向下偏移，小于0时，居中心向上偏移
     * @return
     */
    public int getAreaRectVerticalOffset() {
        return areaRectVerticalOffset;
    }

    /**
     * 设置识别区域垂直方向偏移量，支持负数，大于0时，居中心向下偏移，小于0时，居中心向上偏移
     * @param areaRectVerticalOffset
     * @return
     */
    public DecodeConfig setAreaRectVerticalOffset(int areaRectVerticalOffset) {
        this.areaRectVerticalOffset = areaRectVerticalOffset;
        return this;
    }

    /**
     * 识别区域水平方向偏移量，支持负数，大于0时，居中心向右偏移，小于0时，居中心向左偏移
     * @return
     */
    public int getAreaRectHorizontalOffset() {
        return areaRectHorizontalOffset;
    }

    /**
     * 设置识别区域水平方向偏移量，支持负数，大于0时，居中心向右偏移，小于0时，居中心向左偏移
     * @param areaRectHorizontalOffset
     * @return
     */
    public DecodeConfig setAreaRectHorizontalOffset(int areaRectHorizontalOffset) {
        this.areaRectHorizontalOffset = areaRectHorizontalOffset;
        return this;
    }

    @Override
    public String toString() {
        return "DecodeConfig{" +
                "hints=" + hints +
                ", isMultiDecode=" + isMultiDecode +
                ", isSupportLuminanceInvert=" + isSupportLuminanceInvert +
                ", isSupportLuminanceInvertMultiDecode=" + isSupportLuminanceInvertMultiDecode +
                ", isSupportVerticalCode=" + isSupportVerticalCode +
                ", isSupportVerticalCodeMultiDecode=" + isSupportVerticalCodeMultiDecode +
                ", analyzeAreaRect=" + analyzeAreaRect +
                ", isFullAreaScan=" + isFullAreaScan +
                ", areaRectRatio=" + areaRectRatio +
                ", areaRectVerticalOffset=" + areaRectVerticalOffset +
                ", areaRectHorizontalOffset=" + areaRectHorizontalOffset +
                '}';
    }
}
