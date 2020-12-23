package com.king.zxing;

import android.graphics.Rect;

import com.google.zxing.DecodeHintType;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.Map;

import androidx.annotation.FloatRange;


/**
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DecodeConfig {

    private Map<DecodeHintType,Object> hints = DecodeFormatManager.DEFAULT_HINTS;

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
    private boolean isFullAreaScan = true;

    /**
     * 识别区域比例，默认0.9
     */
    private float areaRectRatio = 0.9f;
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
     * 设置需要分析识别区域，当设置了指定的分析区域时，识别区域比例和识别区域相关参数都将无效
     * @param analyzeAreaRect
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
     * 设置是否支持全区域扫码识别，优先级比识别区域比例高
     * @param fullAreaScan 默认为{@code true}
     * @return
     */
    public DecodeConfig setFullAreaScan(boolean fullAreaScan) {
        isFullAreaScan = fullAreaScan;
        return this;
    }

    /**
     * 识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
     * @return
     */
    public float getAreaRectRatio() {
        return areaRectRatio;
    }

    /**
     * 设置识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
     * @param areaRectRatio
     * @return
     */
    public DecodeConfig setAreaRectRatio(@FloatRange(from = 0.5,to = 1.0) float areaRectRatio) {
        this.areaRectRatio = areaRectRatio;
        return this;
    }

    /**
     * 识别区域垂直方向偏移量
     * @return
     */
    public int getAreaRectVerticalOffset() {
        return areaRectVerticalOffset;
    }

    /**
     * 设置识别区域垂直方向偏移量
     * @param areaRectVerticalOffset
     * @return
     */
    public DecodeConfig setAreaRectVerticalOffset(int areaRectVerticalOffset) {
        this.areaRectVerticalOffset = areaRectVerticalOffset;
        return this;
    }

    /**
     * 识别区域水平方向偏移量
     * @return
     */
    public int getAreaRectHorizontalOffset() {
        return areaRectHorizontalOffset;
    }

    /**
     * 设置识别区域水平方向偏移量
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
