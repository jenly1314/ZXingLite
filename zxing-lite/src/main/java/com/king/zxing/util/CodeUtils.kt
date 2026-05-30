/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.king.zxing.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.king.logx.LogX
import com.king.zxing.DecodeFormatManager
import java.util.HashMap

/**
 * 二维码/条形码工具类：主要包括二维码/条形码的解析与生成
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Suppress("unused")
object CodeUtils {

    const val DEFAULT_REQ_WIDTH = 480
    const val DEFAULT_REQ_HEIGHT = 640

    @JvmStatic
    @JvmOverloads
    fun createQRCode(
        content: String,
        size: Int,
        logo: Bitmap? = null,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float = 0.2f,
        hints: Map<EncodeHintType, *>? = buildQRCodeHints(),
        @ColorInt codeColor: Int = Color.BLACK
    ): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    pixels[y * size + x] = if (bitMatrix.get(x, y)) codeColor else Color.WHITE
                }
            }

            var bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            if (logo != null) {
                bitmap = addLogo(bitmap, logo, ratio) ?: return null
            }
            bitmap
        } catch (e: Exception) {
            LogX.w(e)
            null
        }
    }

    private fun addLogo(src: Bitmap?, logo: Bitmap?, @FloatRange(from = 0.0, to = 1.0) ratio: Float): Bitmap? {
        if (src == null) {
            return null
        }
        if (logo == null) {
            return src
        }

        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height

        if (srcWidth == 0 || srcHeight == 0) {
            return null
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src
        }

        val scaleFactor = srcWidth * ratio / logoWidth
        return try {
            val bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2f, srcHeight / 2f)
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2f, (srcHeight - logoHeight) / 2f, null)
            canvas.save()
            canvas.restore()
            bitmap
        } catch (e: Exception) {
            LogX.w(e)
            null
        }
    }

    @JvmStatic
    fun parseQRCode(bitmapPath: String): String? {
        return parseQRCodeResult(bitmapPath)?.text
    }

    @JvmStatic
    @JvmOverloads
    fun parseQRCodeResult(bitmapPath: String, reqWidth: Int = DEFAULT_REQ_WIDTH, reqHeight: Int = DEFAULT_REQ_HEIGHT): Result? {
        return parseCodeResult(bitmapPath, reqWidth, reqHeight, DecodeFormatManager.QR_CODE_HINTS)
    }

    @JvmStatic
    @JvmOverloads
    fun parseCode(bitmapPath: String, hints: Map<DecodeHintType, Any>? = DecodeFormatManager.ALL_HINTS): String? {
        return parseCodeResult(bitmapPath, hints = hints)?.text
    }

    @JvmStatic
    fun parseQRCode(bitmap: Bitmap): String? {
        return parseCode(bitmap, DecodeFormatManager.QR_CODE_HINTS)
    }

    @JvmStatic
    @JvmOverloads
    fun parseCode(bitmap: Bitmap, hints: Map<DecodeHintType, Any>? = DecodeFormatManager.ALL_HINTS): String? {
        return parseCodeResult(bitmap, hints)?.text
    }

    @JvmStatic
    @JvmOverloads
    fun parseCodeResult(
        bitmapPath: String,
        reqWidth: Int = DEFAULT_REQ_WIDTH,
        reqHeight: Int = DEFAULT_REQ_HEIGHT,
        hints: Map<DecodeHintType, Any>? = DecodeFormatManager.ALL_HINTS
    ): Result? {
        val bitmap = compressBitmap(bitmapPath, reqWidth, reqHeight) ?: return null
        return parseCodeResult(bitmap, hints)
    }

    @JvmStatic
    @JvmOverloads
    fun parseCodeResult(bitmap: Bitmap, hints: Map<DecodeHintType, Any>? = DecodeFormatManager.ALL_HINTS): Result? {
        return parseCodeResult(getRGBLuminanceSource(bitmap), hints)
    }

    @JvmStatic
    @JvmOverloads
    fun parseCodeResult(source: LuminanceSource?, hints: Map<DecodeHintType, Any>? = DecodeFormatManager.ALL_HINTS): Result? {
        var result: Result? = null
        val reader = MultiFormatReader()
        try {
            reader.setHints(hints)
            if (source != null) {
                result = decodeInternal(reader, source)
                if (result == null) {
                    result = decodeInternal(reader, source.invert())
                }
                if (result == null && source.isRotateSupported) {
                    result = decodeInternal(reader, source.rotateCounterClockwise())
                }
            }
        } catch (e: Exception) {
            LogX.w(e)
        } finally {
            reader.reset()
        }
        return result
    }

    private fun decodeInternal(reader: MultiFormatReader, source: LuminanceSource): Result? {
        var result: Result? = null
        try {
            try {
                result = reader.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
            } catch (_: Exception) {
            }
            if (result == null) {
                result = reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source)))
            }
        } catch (_: Exception) {
        }
        return result
    }

    private fun compressBitmap(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        if (reqWidth > 0 && reqHeight > 0) {
            val newOpts = BitmapFactory.Options()
            newOpts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, newOpts)
            newOpts.inSampleSize = getSampleSize(reqWidth, reqHeight, newOpts)
            newOpts.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(path, newOpts)
        }
        return BitmapFactory.decodeFile(path)
    }

    private fun getSampleSize(reqWidth: Int, reqHeight: Int, newOpts: BitmapFactory.Options): Int {
        val width = newOpts.outWidth.toFloat()
        val height = newOpts.outHeight.toFloat()
        var wSize = 1
        if (width > reqWidth) {
            wSize = (width / reqWidth).toInt()
        }
        var hSize = 1
        if (height > reqHeight) {
            hSize = (height / reqHeight).toInt()
        }
        var size = maxOf(wSize, hSize)
        if (size <= 0) {
            size = 1
        }
        return size
    }

    private fun getRGBLuminanceSource(bitmap: Bitmap): RGBLuminanceSource {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return RGBLuminanceSource(width, height, pixels)
    }

    @JvmStatic
    @JvmOverloads
    fun createBarCode(
        content: String,
        desiredWidth: Int,
        desiredHeight: Int,
        hints: Map<EncodeHintType, *>? = null,
        isShowText: Boolean = false,
        textSize: Int = 40,
        @ColorInt codeColor: Int = Color.BLACK
    ): Bitmap? {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, hints, isShowText, textSize, codeColor)
    }

    @JvmStatic
    @JvmOverloads
    fun createBarCode(
        content: String,
        format: BarcodeFormat,
        desiredWidth: Int,
        desiredHeight: Int,
        hints: Map<EncodeHintType, *>? = null,
        isShowText: Boolean = false,
        textSize: Int = 40,
        @ColorInt codeColor: Int = Color.BLACK
    ): Bitmap? {
        if (TextUtils.isEmpty(content)) {
            return null
        }
        val white = Color.WHITE
        val writer = MultiFormatWriter()
        return try {
            val result = writer.encode(content, format, desiredWidth, desiredHeight, hints)
            val width = result.width
            val height = result.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (result.get(x, y)) codeColor else white
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            if (isShowText) {
                addCode(bitmap, content, textSize, codeColor, textSize / 2)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            LogX.w(e)
            null
        }
    }

    private fun addCode(src: Bitmap?, code: String?, textSize: Int, @ColorInt textColor: Int, offset: Int): Bitmap? {
        if (src == null) {
            return null
        }
        if (TextUtils.isEmpty(code)) {
            return src
        }

        val srcWidth = src.width
        val srcHeight = src.height
        if (srcWidth <= 0 || srcHeight <= 0) {
            return null
        }

        return try {
            val bitmap = Bitmap.createBitmap(srcWidth, srcHeight + textSize + offset * 2, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(src, 0f, 0f, null)
            val paint = TextPaint()
            paint.textSize = textSize.toFloat()
            paint.color = textColor
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(code!!, srcWidth / 2f, srcHeight + textSize / 2f + offset, paint)
            canvas.save()
            canvas.restore()
            bitmap
        } catch (e: Exception) {
            LogX.w(e)
            null
        }
    }

    private fun buildQRCodeHints(): Map<EncodeHintType, Any> {
        return HashMap<EncodeHintType, Any>().apply {
            put(EncodeHintType.CHARACTER_SET, "utf-8")
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, 1)
        }
    }
}
