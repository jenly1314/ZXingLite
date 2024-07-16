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
package com.king.zxing.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.king.logx.LogX;
import com.king.zxing.DecodeFormatManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 二维码/条形码工具类：主要包括二维码/条形码的解析与生成
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@SuppressWarnings("unused")
public final class CodeUtils {

    public static final int DEFAULT_REQ_WIDTH = 480;
    public static final int DEFAULT_REQ_HEIGHT = 640;

    private CodeUtils() {
        throw new AssertionError();
    }

    /**
     * 生成二维码
     *
     * @param content 二维码的内容
     * @param size    二维码的大小
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size) {
        return createQRCode(content, size, null);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param size      二维码的大小
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @ColorInt int codeColor) {
        return createQRCode(content, size, null, codeColor);
    }

    /**
     * 生成我二维码
     *
     * @param content 二维码的内容
     * @param size    二维码的大小
     * @param logo    Logo大小默认占二维码的20%
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo) {
        return createQRCode(content, size, logo, Color.BLACK);
    }

    /**
     * 生成我二维码
     *
     * @param content   二维码的内容
     * @param size      二维码的大小
     * @param logo      Logo大小默认占二维码的20%
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo, @ColorInt int codeColor) {
        return createQRCode(content, size, logo, 0.2f, codeColor);
    }

    /**
     * 生成二维码
     *
     * @param content 二维码的内容
     * @param size    二维码的大小
     * @param logo    二维码中间的Logo
     * @param ratio   Logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo, @FloatRange(from = 0.0f, to = 1.0f) float ratio) {
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4
        return createQRCode(content, size, logo, ratio, hints);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param size      二维码的大小
     * @param logo      二维码中间的Logo
     * @param ratio     Logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo, @FloatRange(from = 0.0f, to = 1.0f) float ratio, @ColorInt int codeColor) {
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4
        return createQRCode(content, size, logo, ratio, hints, codeColor);
    }

    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo, @FloatRange(from = 0.0f, to = 1.0f) float ratio, @Nullable Map<EncodeHintType, ?> hints) {
        return createQRCode(content, size, logo, ratio, hints, Color.BLACK);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param size      二维码的大小
     * @param logo      二维码中间的Logo
     * @param ratio     Logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param hints
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo, @FloatRange(from = 0.0f, to = 1.0f) float ratio, @Nullable Map<EncodeHintType, ?> hints, @ColorInt int codeColor) {
        try {

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = codeColor;
                    } else {
                        pixels[y * size + x] = Color.WHITE;
                    }
                }
            }

            // 生成二维码图片的格式
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

            if (logo != null) {
                bitmap = addLogo(bitmap, logo, ratio);
            }

            return bitmap;
        } catch (Exception e) {
            LogX.w(e);
        }

        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     *
     * @param src   原图
     * @param logo  中间的Logo
     * @param ratio Logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return
     */
    private static Bitmap addLogo(@Nullable Bitmap src, @Nullable Bitmap logo, @FloatRange(from = 0.0f, to = 1.0f) float ratio) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小
        float scaleFactor = srcWidth * ratio / logoWidth;
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2f, srcHeight / 2f);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2f, (srcHeight - logoHeight) / 2f, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            LogX.w(e);
        }

        return bitmap;
    }

    /**
     * 解析二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @return
     */
    @Nullable
    public static String parseQRCode(@NonNull String bitmapPath) {
        Result result = parseQRCodeResult(bitmapPath);
        if (result != null) {
            return result.getText();
        }
        return null;
    }

    /**
     * 解析二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @return
     */
    @Nullable
    public static Result parseQRCodeResult(@NonNull String bitmapPath) {
        return parseQRCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT);
    }

    /**
     * 解析二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param reqWidth   请求目标宽度，如果实际图片宽度大于此值，会自动进行压缩处理，当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @param reqHeight  请求目标高度，如果实际图片高度大于此值，会自动进行压缩处理，当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @return
     */
    @Nullable
    public static Result parseQRCodeResult(@NonNull String bitmapPath, int reqWidth, int reqHeight) {
        return parseCodeResult(bitmapPath, reqWidth, reqHeight, DecodeFormatManager.QR_CODE_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @return
     */
    @Nullable
    public static String parseCode(@NonNull String bitmapPath) {
        return parseCode(bitmapPath, DecodeFormatManager.ALL_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param hints      解析编码类型
     * @return
     */
    @Nullable
    public static String parseCode(@NonNull String bitmapPath, @Nullable Map<DecodeHintType, Object> hints) {
        Result result = parseCodeResult(bitmapPath, hints);
        if (result != null) {
            return result.getText();
        }
        return null;
    }

    /**
     * 解析二维码图片
     *
     * @param bitmap 解析的图片
     * @return
     */
    @Nullable
    public static String parseQRCode(@NonNull Bitmap bitmap) {
        return parseCode(bitmap, DecodeFormatManager.QR_CODE_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @return
     */
    @Nullable
    public static String parseCode(@NonNull Bitmap bitmap) {
        return parseCode(bitmap, DecodeFormatManager.ALL_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @param hints  解析编码类型
     * @return
     */
    @Nullable
    public static String parseCode(@NonNull Bitmap bitmap, @Nullable Map<DecodeHintType, Object> hints) {
        Result result = parseCodeResult(bitmap, hints);
        if (result != null) {
            return result.getText();
        }
        return null;
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath
     * @param hints      解析编码类型
     * @return
     */
    @Nullable
    public static Result parseCodeResult(@NonNull String bitmapPath, @Nullable Map<DecodeHintType, Object> hints) {
        return parseCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT, hints);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param reqWidth   请求目标宽度，如果实际图片宽度大于此值，会自动进行压缩处理，当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @param reqHeight  请求目标高度，如果实际图片高度大于此值，会自动进行压缩处理，当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @param hints      解析编码类型
     * @return
     */
    @Nullable
    public static Result parseCodeResult(@NonNull String bitmapPath, int reqWidth, int reqHeight, @Nullable Map<DecodeHintType, Object> hints) {
        return parseCodeResult(compressBitmap(bitmapPath, reqWidth, reqHeight), hints);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @return
     */
    @Nullable
    public static Result parseCodeResult(@NonNull Bitmap bitmap) {
        return parseCodeResult(getRGBLuminanceSource(bitmap), DecodeFormatManager.ALL_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @param hints  解析编码类型
     * @return
     */
    @Nullable
    public static Result parseCodeResult(@NonNull Bitmap bitmap, @Nullable Map<DecodeHintType, Object> hints) {
        return parseCodeResult(getRGBLuminanceSource(bitmap), hints);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param source
     * @param hints
     * @return
     */
    @Nullable
    public static Result parseCodeResult(LuminanceSource source, @Nullable Map<DecodeHintType, Object> hints) {
        Result result = null;
        MultiFormatReader reader = new MultiFormatReader();
        try {
            reader.setHints(hints);
            if (source != null) {
                result = decodeInternal(reader, source);
                if (result == null) {
                    result = decodeInternal(reader, source.invert());
                }
                if (result == null && source.isRotateSupported()) {
                    result = decodeInternal(reader, source.rotateCounterClockwise());
                }
            }

        } catch (Exception e) {
            LogX.w(e);
        } finally {
            reader.reset();
        }

        return result;
    }

    @Nullable
    private static Result decodeInternal(MultiFormatReader reader, LuminanceSource source) {
        Result result = null;
        try {
            try {
                //采用HybridBinarizer解析
                result = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
            } catch (Exception ignored) {

            }
            if (result == null) {
                //如果没有解析成功，再采用GlobalHistogramBinarizer解析一次
                result = reader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
            }
        } catch (Exception ignored) {

        }
        return result;
    }


    /**
     * 压缩图片
     *
     * @param path
     * @return
     */
    private static Bitmap compressBitmap(@NonNull String path, int reqWidth, int reqHeight) {
        if (reqWidth > 0 && reqHeight > 0) {//都大于进行判断是否压缩

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;//获取原始图片大小
            BitmapFactory.decodeFile(path, newOpts);// 此时返回bm为空
            newOpts.inSampleSize = getSampleSize(reqWidth, reqHeight, newOpts);
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            newOpts.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(path, newOpts);

        }

        return BitmapFactory.decodeFile(path);
    }

    /**
     * @param reqWidth
     * @param reqHeight
     * @param newOpts
     * @return
     */
    private static int getSampleSize(int reqWidth, int reqHeight,@NonNull  BitmapFactory.Options newOpts) {
        float width = newOpts.outWidth;
        float height = newOpts.outHeight;
        // 缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int wSize = 1;// wSize=1表示不缩放
        if (width > reqWidth) {// 如果宽度大的话根据宽度固定大小缩放
            wSize = (int) (width / reqWidth);
        }
        int hSize = 1;// wSize=1表示不缩放
        if (height > reqHeight) {// 如果高度高的话根据宽度固定大小缩放
            hSize = (int) (height / reqHeight);
        }
        int size = Math.max(wSize, hSize);
        if (size <= 0)
            size = 1;
        return size;
    }


    /**
     * 获取RGBLuminanceSource
     *
     * @param bitmap
     * @return
     */
    private static RGBLuminanceSource getRGBLuminanceSource(@NonNull Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return new RGBLuminanceSource(width, height, pixels);

    }

    /**
     * 生成条形码
     *
     * @param content
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, int desiredWidth, int desiredHeight) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, null);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, @NonNull BarcodeFormat format, int desiredWidth, int desiredHeight) {
        return createBarCode(content, format, desiredWidth, desiredHeight, null);
    }

    public static Bitmap createBarCode(@NonNull String content, int desiredWidth, int desiredHeight, boolean isShowText) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, null, isShowText, 40, Color.BLACK);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param desiredWidth
     * @param desiredHeight
     * @param isShowText
     * @param codeColor
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, int desiredWidth, int desiredHeight, boolean isShowText, @ColorInt int codeColor) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, null, isShowText, 40, codeColor);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, @NonNull BarcodeFormat format, int desiredWidth, int desiredHeight, @Nullable Map<EncodeHintType, ?> hints) {
        return createBarCode(content, format, desiredWidth, desiredHeight, hints, false, 40, Color.BLACK);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @param isShowText
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, @NonNull BarcodeFormat format, int desiredWidth, int desiredHeight, @Nullable Map<EncodeHintType, ?> hints, boolean isShowText) {
        return createBarCode(content, format, desiredWidth, desiredHeight, hints, isShowText, 40, Color.BLACK);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param isShowText
     * @param codeColor
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, @NonNull BarcodeFormat format, int desiredWidth, int desiredHeight, boolean isShowText, @ColorInt int codeColor) {
        return createBarCode(content, format, desiredWidth, desiredHeight, null, isShowText, 40, codeColor);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @param isShowText
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, @NonNull BarcodeFormat format, int desiredWidth, int desiredHeight, @Nullable Map<EncodeHintType, ?> hints, boolean isShowText, @ColorInt int codeColor) {
        return createBarCode(content, format, desiredWidth, desiredHeight, hints, isShowText, 40, codeColor);
    }

    /**
     * 生成条形码
     *
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @param isShowText
     * @param textSize
     * @param codeColor
     * @return
     */
    public static Bitmap createBarCode(@NonNull String content, @NonNull BarcodeFormat format, int desiredWidth, int desiredHeight, @Nullable Map<EncodeHintType, ?> hints, boolean isShowText, int textSize, @ColorInt int codeColor) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        final int WHITE = Color.WHITE;

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix result = writer.encode(content, format, desiredWidth,
                    desiredHeight, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? codeColor : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (isShowText) {
                return addCode(bitmap, content, textSize, codeColor, textSize / 2);
            }
            return bitmap;
        } catch (Exception e) {
            LogX.w(e);
        }
        return null;
    }

    /**
     * 条形码下面添加文本信息
     *
     * @param src
     * @param code
     * @param textSize
     * @param textColor
     * @return
     */
    private static Bitmap addCode(@Nullable Bitmap src, @Nullable String code, int textSize, @ColorInt int textColor, int offset) {
        if (src == null) {
            return null;
        }

        if (TextUtils.isEmpty(code)) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        if (srcWidth <= 0 || srcHeight <= 0) {
            return null;
        }

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(srcWidth, srcHeight + textSize + offset * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            TextPaint paint = new TextPaint();
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(code, srcWidth / 2f, srcHeight + textSize / 2f + offset, paint);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            LogX.w(e);
        }

        return bitmap;
    }


}