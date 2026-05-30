package com.king.zxing.analyze

import android.graphics.Rect
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import com.king.zxing.DecodeConfig
import com.king.zxing.DecodeFormatManager

/**
 * 矩阵区域分析器：主要用于锁定具体的识别区域
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
abstract class AreaRectAnalyzer(config: DecodeConfig?) : ImageAnalyzer() {

    protected var mDecodeConfig: DecodeConfig? = config
    protected var mHints: Map<DecodeHintType, Any>? = null
    protected var isMultiDecode = true
    private var mAreaRectRatio = DecodeConfig.DEFAULT_AREA_RECT_RATIO
    private var mAreaRectHorizontalOffset = 0
    private var mAreaRectVerticalOffset = 0

    init {
        if (config != null) {
            mHints = config.getHints()
            isMultiDecode = config.isMultiDecode()
            mAreaRectRatio = config.getAreaRectRatio()
            mAreaRectHorizontalOffset = config.getAreaRectHorizontalOffset()
            mAreaRectVerticalOffset = config.getAreaRectVerticalOffset()
        } else {
            mHints = DecodeFormatManager.DEFAULT_HINTS
        }
    }

    override fun analyze(data: ByteArray, width: Int, height: Int): Result? {
        if (mDecodeConfig != null) {
            if (mDecodeConfig!!.isFullAreaScan()) {
                return analyze(data, width, height, 0, 0, width, height)
            }

            val rect: Rect? = mDecodeConfig!!.getAnalyzeAreaRect()
            if (rect != null) {
                return analyze(data, width, height, rect.left, rect.top, rect.width(), rect.height())
            }
        }

        val size = (minOf(width, height) * mAreaRectRatio).toInt()
        val left = (width - size) / 2 + mAreaRectHorizontalOffset
        val top = (height - size) / 2 + mAreaRectVerticalOffset

        return analyze(data, width, height, left, top, size, size)
    }

    abstract fun analyze(
        data: ByteArray,
        dataWidth: Int,
        dataHeight: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int
    ): Result?
}
