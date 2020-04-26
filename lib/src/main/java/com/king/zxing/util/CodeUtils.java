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
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.king.zxing.CaptureHelper;
import com.king.zxing.DecodeFormatManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class CodeUtils {

    private CodeUtils(){
        throw new AssertionError();
    }

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix) {
        return createQRCode(content,heightPix,null);
    }

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix,int codeColor) {
        return createQRCode(content,heightPix,null,codeColor);
    }

    /**
     * 生成我二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @param logo logo大小默认占二维码的20%
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo) {
        return createQRCode(content,heightPix,logo,Color.BLACK);
    }

    /**
     * 生成我二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @param logo logo大小默认占二维码的20%
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,int codeColor) {
        return createQRCode(content,heightPix,logo,0.2f,codeColor);
    }

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @param logo 二维码中间的logo
     * @param ratio  logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,@FloatRange(from = 0.0f,to = 1.0f)float ratio) {
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put( EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4
        return createQRCode(content,heightPix,logo,ratio,hints);
    }

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @param logo 二维码中间的logo
     * @param ratio logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,@FloatRange(from = 0.0f,to = 1.0f)float ratio,int codeColor) {
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put( EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1); //default is 4
        return createQRCode(content,heightPix,logo,ratio,hints,codeColor);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,@FloatRange(from = 0.0f,to = 1.0f)float ratio,Map<EncodeHintType,?> hints) {
        return createQRCode(content,heightPix,logo,ratio,hints,Color.BLACK);
    }

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPix 二维码的高
     * @param logo 二维码中间的logo
     * @param ratio  logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param hints
     * @param codeColor 二维码的颜色
     * @return
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,@FloatRange(from = 0.0f,to = 1.0f)float ratio,Map<EncodeHintType,?> hints,int codeColor) {
        try {

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, heightPix, heightPix, hints);
            int[] pixels = new int[heightPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < heightPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * heightPix + x] = codeColor;
                    } else {
                        pixels[y * heightPix + x] = Color.WHITE;
                    }
                }
            }

            // 生成二维码图片的格式
            Bitmap bitmap = Bitmap.createBitmap(heightPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, heightPix, 0, 0, heightPix, heightPix);

            if (logo != null) {
                bitmap = addLogo(bitmap, logo,ratio);
            }

            return bitmap;
        } catch (WriterException e) {
            Log.w(CaptureHelper.TAG,e.getMessage());
        }

        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     * @param src
     * @param logo
     * @param ratio  logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo,@FloatRange(from = 0.0f,to = 1.0f) float ratio) {
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
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            Log.w(CaptureHelper.TAG,e.getMessage());
        }

        return bitmap;
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @return
     */
    public static String parseQRCode(String bitmapPath) {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return parseQRCode(bitmapPath,hints);
    }

    /**
     * 解析二维码图片
     * @param bitmap
     * @return
     */
    public static String parseQRCode(Bitmap bitmap) {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return parseQRCode(bitmap,hints);
    }

    /**
     * 解析二维码图片
     * @param bitmap
     * @param hints
     * @return
     */
    public static String parseQRCode(Bitmap bitmap, Map<DecodeHintType,?> hints){
        Result result = parseQRCodeResult(bitmap,hints);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @param hints
     * @return
     */
    public static String parseQRCode(String bitmapPath, Map<DecodeHintType,?> hints){
        Result result = parseQRCodeResult(compressBitmap(bitmapPath),hints);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析二维码图片
     * @param originBitmap
     * @param hints
     * @return
     */
    public static Result parseQRCodeResult(Bitmap originBitmap, Map<DecodeHintType,?> hints){
        Result result = null;
        try{
            QRCodeReader reader = new QRCodeReader();

            RGBLuminanceSource source = getRGBLuminanceSource(originBitmap);
            if (source != null) {

                boolean isReDecode;
                try {
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    result = reader.decode(bitmap,hints);
                    isReDecode = false;
                } catch (Exception e) {
                    isReDecode = true;
                }

                if(isReDecode){
                    try {
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.invert()));
                        result = reader.decode(bitmap,hints);
                        isReDecode = false;
                    } catch (Exception e) {
                        isReDecode = true;
                    }
                }

                if(isReDecode){
                    try{
                        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                        result = reader.decode(bitmap,hints);
                        isReDecode = false;
                    }catch (Exception e){
                        isReDecode = true;
                    }
                }

                if(isReDecode && source.isRotateSupported()){
                    try{
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.rotateCounterClockwise()));
                        result = reader.decode(bitmap,hints);
                    }catch (Exception e){

                    }
                }

                reader.reset();
            }

        }catch (Exception e){
            Log.w(CaptureHelper.TAG,e.getMessage());
        }

        return result;
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @return
     */
    public static String parseCode(String bitmapPath){
        Map<DecodeHintType,Object> hints = new HashMap<>();
        //添加可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);

        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        return parseCode(bitmapPath,hints);
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param hints 解析编码类型
     * @return
     */
    public static String parseCode(String bitmapPath, Map<DecodeHintType,Object> hints){
        Result result = parseCodeResult(compressBitmap(bitmapPath),hints);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmap
     * @return
     */
    public static String parseCode(Bitmap bitmap){
        Map<DecodeHintType,Object> hints = new HashMap<>();
        //添加可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);

        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        return parseCode(bitmap,hints);
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmap
     * @param hints 解析编码类型
     * @return
     */
    public static String parseCode(Bitmap bitmap, Map<DecodeHintType,Object> hints){
        Result result = parseCodeResult(bitmap,hints);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析一维码/二维码图片
     * @param originBitmap
     * @param hints 解析编码类型
     * @return
     */
    public static Result parseCodeResult(Bitmap originBitmap, Map<DecodeHintType,Object> hints){
        Result result = null;
        try{
            MultiFormatReader reader = new MultiFormatReader();
            reader.setHints(hints);

            RGBLuminanceSource source = getRGBLuminanceSource(originBitmap);
            if (source != null) {

                boolean isReDecode;
                try {
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    result = reader.decodeWithState(bitmap);
                    isReDecode = false;
                } catch (Exception e) {
                    isReDecode = true;
                }

                if(isReDecode){
                    try {
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.invert()));
                        result = reader.decodeWithState(bitmap);
                        isReDecode = false;
                    } catch (Exception e) {
                        isReDecode = true;
                    }
                }

                if(isReDecode){
                    try{
                        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                        result = reader.decodeWithState(bitmap);
                        isReDecode = false;
                    }catch (Exception e){
                        isReDecode = true;
                    }
                }

                if(isReDecode && source.isRotateSupported()){
                    try{
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.rotateCounterClockwise()));
                        result = reader.decodeWithState(bitmap);
                    }catch (Exception e){

                    }
                }

                reader.reset();
            }

        }catch (Exception e){
            Log.w(CaptureHelper.TAG,e.getMessage());
        }

        return result;
    }



    /**
     * 压缩图片
     * @param path
     * @return
     */
    private static Bitmap compressBitmap(String path){

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;//获取原始图片大小
        BitmapFactory.decodeFile(path, newOpts);// 此时返回bm为空
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float width = 800f;
        float height = 480f;
        // 缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > width) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        } else if (w < h && h > height) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / height);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, newOpts);
    }

    /**
     * 获取RGBLuminanceSource
     * @param bitmap
     * @return
     */
    private static RGBLuminanceSource getRGBLuminanceSource(@NonNull Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return new RGBLuminanceSource(width, height, pixels);

    }

    /**
     * 生成条形码
     * @param content
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight) {
        return createBarCode(content,BarcodeFormat.CODE_128,desiredWidth,desiredHeight,null);
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap createBarCode(String content,BarcodeFormat format, int desiredWidth, int desiredHeight) {
        return createBarCode(content,format,desiredWidth,desiredHeight,null);
    }

    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight, boolean isShowText) {
        return createBarCode(content,BarcodeFormat.CODE_128,desiredWidth,desiredHeight,null,isShowText,40,Color.BLACK);
    }

    /**
     * 生成条形码
     * @param content
     * @param desiredWidth
     * @param desiredHeight
     * @param isShowText
     * @param codeColor
     * @return
     */
    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight, boolean isShowText,@ColorInt int codeColor) {
        return createBarCode(content,BarcodeFormat.CODE_128,desiredWidth,desiredHeight,null,isShowText,40,codeColor);
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @return
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType,?> hints) {
        return createBarCode(content,format,desiredWidth,desiredHeight,hints,false,40,Color.BLACK);
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @param isShowText
     * @return
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType,?> hints, boolean isShowText) {
        return createBarCode(content,format,desiredWidth,desiredHeight,hints,isShowText,40,Color.BLACK);
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param isShowText
     * @param codeColor
     * @return
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight,  boolean isShowText,@ColorInt int codeColor) {
        return createBarCode(content,format,desiredWidth,desiredHeight,null,isShowText,40,codeColor);
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @param isShowText
     * @return
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType,?> hints, boolean isShowText,@ColorInt int codeColor) {
        return createBarCode(content,format,desiredWidth,desiredHeight,hints,isShowText,40,codeColor);
    }

    /**
     * 生成条形码
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
    public static Bitmap createBarCode(String content,BarcodeFormat format, int desiredWidth, int desiredHeight,Map<EncodeHintType,?> hints,boolean isShowText,int textSize,@ColorInt int codeColor) {
        if(TextUtils.isEmpty(content)){
            return null;
        }
        final int WHITE = Color.WHITE;
        final int BLACK = codeColor;

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
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if(isShowText){
                return addCode(bitmap,content,textSize,codeColor,textSize/2);
            }
            return bitmap;
        } catch (WriterException e) {
            Log.w(CaptureHelper.TAG,e.getMessage());
        }
        return null;
    }

    /**
     * 条形码下面添加文本信息
     * @param src
     * @param code
     * @param textSize
     * @param textColor
     * @return
     */
    private static Bitmap addCode(Bitmap src,String code,int textSize,@ColorInt int textColor,int offset) {
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

        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight + textSize + offset * 2, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            TextPaint paint = new TextPaint();
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(code,srcWidth/2,srcHeight + textSize /2 + offset,paint);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            Log.w(CaptureHelper.TAG,e.getMessage());
        }

        return bitmap;
    }

}
