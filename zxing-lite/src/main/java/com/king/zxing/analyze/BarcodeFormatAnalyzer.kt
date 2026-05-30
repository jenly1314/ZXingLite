package com.king.zxing.analyze

import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.king.logx.LogX
import com.king.zxing.DecodeConfig

/**
 * 条码分析器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Suppress("unused")
abstract class BarcodeFormatAnalyzer(config: DecodeConfig?) : AreaRectAnalyzer(config) {

    private var mReader: Reader? = null

    constructor(hints: Map<DecodeHintType, Any>?) : this(DecodeConfig().setHints(hints))

    init {
        initReader()
    }

    private fun initReader() {
        mReader = createReader()
    }

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
        if (mReader != null) {
            try {
                val start = System.currentTimeMillis()
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

                    if (mDecodeConfig!!.isSupportLuminanceInvert()) {
                        rawResult = decodeInternal(source.invert(), mDecodeConfig!!.isSupportLuminanceInvertMultiDecode())
                    }
                }
                if (rawResult != null) {
                    val end = System.currentTimeMillis()
                    LogX.d("Found barcode in ${end - start} ms")
                }
            } catch (_: Exception) {
            } finally {
                mReader?.reset()
            }
        }
        return rawResult
    }

    private fun decodeInternal(source: LuminanceSource, isMultiDecode: Boolean): Result? {
        var result: Result? = null
        try {
            try {
                result = mReader?.decode(BinaryBitmap(HybridBinarizer(source)), mHints)
            } catch (_: Exception) {
            }
            if (isMultiDecode && result == null) {
                result = mReader?.decode(BinaryBitmap(GlobalHistogramBinarizer(source)), mHints)
            }
        } catch (_: Exception) {
        }
        return result
    }

    abstract fun createReader(): Reader
}
