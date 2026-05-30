package com.king.zxing

import android.graphics.Rect
import androidx.annotation.FloatRange
import com.google.zxing.DecodeHintType

/**
 * 解码配置：主要用于在扫码识别时，提供一些配置，便于扩展。通过配置可决定内置分析器的能力，从而间接的控制并简化扫码识别的流程
 * <p>
 * 设置解码 [setHints]内置的一些解码可参见如下：
 * <p>
 * [DecodeFormatManager.DEFAULT_HINTS]
 * [DecodeFormatManager.ALL_HINTS]
 * [DecodeFormatManager.CODE_128_HINTS]
 * [DecodeFormatManager.QR_CODE_HINTS]
 * [DecodeFormatManager.ONE_DIMENSIONAL_HINTS]
 * [DecodeFormatManager.TWO_DIMENSIONAL_HINTS]
 * [DecodeFormatManager.DEFAULT_HINTS]
 * <p>
 *
 * <p>
 * 如果不满足您也可以通过[DecodeFormatManager.createDecodeHints]自己配置支持的格式
 *
 * <p>
 * 识别区域可设置的方式有如下几种：
 * [setFullAreaScan] 设置是否支持全区域扫码识别，优先级比识别区域高
 * [setAnalyzeAreaRect] 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
 * [setAreaRectRatio] 设置识别区域比例，默认[DEFAULT_AREA_RECT_RATIO]，设置的比例最终会基于分析图像帧上裁减出此比例的一个矩形进行扫码识别，优先级最低
 * <p>
 * 以上几种识别区域都是基于[androidx.camera.core.ImageAnalysis] 配置的分析目标分辨率作为参照的；请注意区分 [androidx.camera.core.Preview] 与 [androidx.camera.core.ImageAnalysis]配置的区别。
 * <p>
 * 即判定区域分析的优先级顺序为:[setFullAreaScan] -> [setAnalyzeAreaRect] -> [setAreaRectRatio]
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Suppress("unused")
open class DecodeConfig {

    private var hints: Map<DecodeHintType, Any>? = DecodeFormatManager.DEFAULT_HINTS
    private var isMultiDecode = true
    private var isSupportLuminanceInvert = false
    private var isSupportLuminanceInvertMultiDecode = false
    private var isSupportVerticalCode = false
    private var isSupportVerticalCodeMultiDecode = false
    private var analyzeAreaRect: Rect? = null
    private var isFullAreaScan = false
    private var areaRectRatio = DEFAULT_AREA_RECT_RATIO
    private var areaRectVerticalOffset = 0
    private var areaRectHorizontalOffset = 0

    fun getHints(): Map<DecodeHintType, Any>? {
        return hints
    }

    fun setHints(hints: Map<DecodeHintType, Any>?): DecodeConfig {
        this.hints = hints
        return this
    }

    fun isSupportLuminanceInvert(): Boolean {
        return isSupportLuminanceInvert
    }

    fun setSupportLuminanceInvert(supportLuminanceInvert: Boolean): DecodeConfig {
        isSupportLuminanceInvert = supportLuminanceInvert
        return this
    }

    fun isSupportVerticalCode(): Boolean {
        return isSupportVerticalCode
    }

    fun setSupportVerticalCode(supportVerticalCode: Boolean): DecodeConfig {
        isSupportVerticalCode = supportVerticalCode
        return this
    }

    fun isMultiDecode(): Boolean {
        return isMultiDecode
    }

    fun setMultiDecode(multiDecode: Boolean): DecodeConfig {
        isMultiDecode = multiDecode
        return this
    }

    fun isSupportLuminanceInvertMultiDecode(): Boolean {
        return isSupportLuminanceInvertMultiDecode
    }

    fun setSupportLuminanceInvertMultiDecode(supportLuminanceInvertMultiDecode: Boolean): DecodeConfig {
        isSupportLuminanceInvertMultiDecode = supportLuminanceInvertMultiDecode
        return this
    }

    fun isSupportVerticalCodeMultiDecode(): Boolean {
        return isSupportVerticalCodeMultiDecode
    }

    fun setSupportVerticalCodeMultiDecode(supportVerticalCodeMultiDecode: Boolean): DecodeConfig {
        isSupportVerticalCodeMultiDecode = supportVerticalCodeMultiDecode
        return this
    }

    fun getAnalyzeAreaRect(): Rect? {
        return analyzeAreaRect
    }

    fun setAnalyzeAreaRect(analyzeAreaRect: Rect?): DecodeConfig {
        this.analyzeAreaRect = analyzeAreaRect
        return this
    }

    fun isFullAreaScan(): Boolean {
        return isFullAreaScan
    }

    fun setFullAreaScan(fullAreaScan: Boolean): DecodeConfig {
        isFullAreaScan = fullAreaScan
        return this
    }

    fun getAreaRectRatio(): Float {
        return areaRectRatio
    }

    fun setAreaRectRatio(@FloatRange(from = 0.5, to = 1.0) areaRectRatio: Float): DecodeConfig {
        this.areaRectRatio = areaRectRatio
        return this
    }

    fun getAreaRectVerticalOffset(): Int {
        return areaRectVerticalOffset
    }

    fun setAreaRectVerticalOffset(areaRectVerticalOffset: Int): DecodeConfig {
        this.areaRectVerticalOffset = areaRectVerticalOffset
        return this
    }

    fun getAreaRectHorizontalOffset(): Int {
        return areaRectHorizontalOffset
    }

    fun setAreaRectHorizontalOffset(areaRectHorizontalOffset: Int): DecodeConfig {
        this.areaRectHorizontalOffset = areaRectHorizontalOffset
        return this
    }

    override fun toString(): String {
        return "DecodeConfig{" +
            "hints=$hints" +
            ", isMultiDecode=$isMultiDecode" +
            ", isSupportLuminanceInvert=$isSupportLuminanceInvert" +
            ", isSupportLuminanceInvertMultiDecode=$isSupportLuminanceInvertMultiDecode" +
            ", isSupportVerticalCode=$isSupportVerticalCode" +
            ", isSupportVerticalCodeMultiDecode=$isSupportVerticalCodeMultiDecode" +
            ", analyzeAreaRect=$analyzeAreaRect" +
            ", isFullAreaScan=$isFullAreaScan" +
            ", areaRectRatio=$areaRectRatio" +
            ", areaRectVerticalOffset=$areaRectVerticalOffset" +
            ", areaRectHorizontalOffset=$areaRectHorizontalOffset" +
            '}'
    }

    companion object {
        const val DEFAULT_AREA_RECT_RATIO = 0.8f
    }
}
