package com.king.zxing

import androidx.annotation.NonNull
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.EnumMap

/**
 * 解码格式管理器
 * <p>
 * 将常见的一些解码配置已根据条形码类型进行了几大划分，可根据需要找到符合的划分配置类型直接使用。
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
object DecodeFormatManager {

    @JvmField
    val ALL_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    @JvmField
    val CODE_128_HINTS: MutableMap<DecodeHintType, Any> = createDecodeHint(BarcodeFormat.CODE_128)

    @JvmField
    val QR_CODE_HINTS: MutableMap<DecodeHintType, Any> = createDecodeHint(BarcodeFormat.QR_CODE)

    @JvmField
    val ONE_DIMENSIONAL_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    @JvmField
    val TWO_DIMENSIONAL_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    @JvmField
    val DEFAULT_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    init {
        addDecodeHintTypes(ALL_HINTS, getAllFormats())
        addDecodeHintTypes(ONE_DIMENSIONAL_HINTS, getOneDimensionalFormats())
        addDecodeHintTypes(TWO_DIMENSIONAL_HINTS, getTwoDimensionalFormats())
        addDecodeHintTypes(DEFAULT_HINTS, getDefaultFormats())
    }

    private fun getAllFormats(): List<BarcodeFormat> {
        return ArrayList<BarcodeFormat>().apply {
            add(BarcodeFormat.AZTEC)
            add(BarcodeFormat.CODABAR)
            add(BarcodeFormat.CODE_39)
            add(BarcodeFormat.CODE_93)
            add(BarcodeFormat.CODE_128)
            add(BarcodeFormat.DATA_MATRIX)
            add(BarcodeFormat.EAN_8)
            add(BarcodeFormat.EAN_13)
            add(BarcodeFormat.ITF)
            add(BarcodeFormat.MAXICODE)
            add(BarcodeFormat.PDF_417)
            add(BarcodeFormat.QR_CODE)
            add(BarcodeFormat.RSS_14)
            add(BarcodeFormat.RSS_EXPANDED)
            add(BarcodeFormat.UPC_A)
            add(BarcodeFormat.UPC_E)
            add(BarcodeFormat.UPC_EAN_EXTENSION)
        }
    }

    private fun getOneDimensionalFormats(): List<BarcodeFormat> {
        return ArrayList<BarcodeFormat>().apply {
            add(BarcodeFormat.CODABAR)
            add(BarcodeFormat.CODE_39)
            add(BarcodeFormat.CODE_93)
            add(BarcodeFormat.CODE_128)
            add(BarcodeFormat.EAN_8)
            add(BarcodeFormat.EAN_13)
            add(BarcodeFormat.ITF)
            add(BarcodeFormat.RSS_14)
            add(BarcodeFormat.RSS_EXPANDED)
            add(BarcodeFormat.UPC_A)
            add(BarcodeFormat.UPC_E)
            add(BarcodeFormat.UPC_EAN_EXTENSION)
        }
    }

    private fun getTwoDimensionalFormats(): List<BarcodeFormat> {
        return ArrayList<BarcodeFormat>().apply {
            add(BarcodeFormat.AZTEC)
            add(BarcodeFormat.DATA_MATRIX)
            add(BarcodeFormat.MAXICODE)
            add(BarcodeFormat.PDF_417)
            add(BarcodeFormat.QR_CODE)
        }
    }

    private fun getDefaultFormats(): List<BarcodeFormat> {
        return ArrayList<BarcodeFormat>().apply {
            add(BarcodeFormat.QR_CODE)
            add(BarcodeFormat.UPC_A)
            add(BarcodeFormat.EAN_13)
            add(BarcodeFormat.CODE_128)
        }
    }

    @JvmStatic
    fun createDecodeHints(@NonNull vararg barcodeFormats: BarcodeFormat): MutableMap<DecodeHintType, Any> {
        val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
        addDecodeHintTypes(hints, Arrays.asList(*barcodeFormats))
        return hints
    }

    @JvmStatic
    fun createDecodeHint(@NonNull barcodeFormat: BarcodeFormat): MutableMap<DecodeHintType, Any> {
        val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
        addDecodeHintTypes(hints, Collections.singletonList(barcodeFormat))
        return hints
    }

    private fun addDecodeHintTypes(hints: MutableMap<DecodeHintType, Any>, formats: List<BarcodeFormat>) {
        hints[DecodeHintType.POSSIBLE_FORMATS] = formats
        hints[DecodeHintType.TRY_HARDER] = true
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
    }
}
