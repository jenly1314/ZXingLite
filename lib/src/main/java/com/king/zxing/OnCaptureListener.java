package com.king.zxing;

import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface OnCaptureListener {


    /**
     * 接收解码后的扫码结果
     * @param result
     * @param barcode
     * @param scaleFactor
     */
    void onHandleDecode(Result result, Bitmap barcode, float scaleFactor);


}
