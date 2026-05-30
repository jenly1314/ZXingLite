package com.king.zxing.analyze

import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.king.logx.LogX
import com.king.zxing.DecodeConfig

/**
 * 多格式分析器：主要用于分析识别条形码/二维码
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Suppress("unused")
open class MultiFormatAnalyzer @JvmOverloads constructor(config: DecodeConfig? = null) : AreaRectAnalyzer(config) {

    private val mReader: MultiFormatReader = MultiFormatReader()

    constructor(hints: Map<DecodeHintType, Any>?) : this(DecodeConfig().setHints(hints))

    override fun analyze(
        data: ByteArray,
        dataWidth: Int,
        dataHeight: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int
    ): Result? {
        var rawResult: Result? = null
        try {
            val start = System.currentTimeMillis()
            mReader.setHints(mHints)
            val source = PlanarYUVLuminanceSource(data, dataWidth, dataHeight, left, top, width, height, false)
            rawResult = decodeInternal(source, isMultiDecode)

            if (rawResult == null && mDecodeConfig != null) {
                if (mDecodeConfig!!.isSupportVerticalCode()) {
                    val rotatedData = ByteArray(data.size)
                    for (y in 0 until dataHeight) {
                        for (x in 0 until dataWidth) {
                            rotatedData[x * dataHeight + dataHeight - y - 1] = data[x + y * dataWidth]
                        }
                    }
                    rawResult = decodeInternal(
                        PlanarYUVLuminanceSource(rotatedData, dataHeight, dataWidth, top, left, height, width, false),
                        mDecodeConfig!!.isSupportVerticalCodeMultiDecode()
                    )
                }

                if (rawResult == null && mDecodeConfig!!.isSupportLuminanceInvert()) {
                    rawResult = decodeInternal(source.invert(), mDecodeConfig!!.isSupportLuminanceInvertMultiDecode())
                }
            }
            if (rawResult != null) {
                val end = System.currentTimeMillis()
                LogX.d("Found barcode in ${end - start} ms")
            }
        } catch (_: Exception) {
        } finally {
            mReader.reset()
        }
        return rawResult
    }

    private fun decodeInternal(source: LuminanceSource, isMultiDecode: Boolean): Result? {
        var result: Result? = null
        try {
            try {
                result = mReader.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
            } catch (_: Exception) {
            }
            if (isMultiDecode && result == null) {
                result = mReader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source)))
            }
        } catch (_: Exception) {
        }
        return result
    }
}
