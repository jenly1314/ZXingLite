package com.king.zxing;
/*
 * Copyright (C) 2010 ZXing authors
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


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.util.Map;

final class DecodeHandler extends Handler {

    private final Context context;
    private final CameraManager cameraManager;
    private final CaptureHandler handler;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;

    private long lastZoomTime;

    DecodeHandler(Context context, CameraManager cameraManager,CaptureHandler handler, Map<DecodeHintType,Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.context = context;
        this.cameraManager = cameraManager;
        this.handler = handler;
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null || !running) {
            return;
        }
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2,isScreenPortrait(),handler.isSupportVerticalCode());

        } else if (message.what == R.id.quit) {
            running = false;
            Looper.myLooper().quit();

        }
    }

    private boolean isScreenPortrait(){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point();
        display.getSize(screenResolution);
        return screenResolution.x < screenResolution.y;
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height,boolean isScreenPortrait,boolean isSupportVerticalCode) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildPlanarYUVLuminanceSource(data,width,height,isScreenPortrait);

        if (source != null) {

            boolean isReDecode;
            try {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                rawResult = multiFormatReader.decodeWithState(bitmap);
                isReDecode = false;
            } catch (Exception e) {
                isReDecode = true;
            }

            if(isReDecode && handler.isSupportLuminanceInvert()){
                try {
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.invert()));
                    rawResult = multiFormatReader.decodeWithState(bitmap);
                    isReDecode = false;
                } catch (Exception e) {
                    isReDecode = true;
                }
            }

            if(isReDecode){
                try{
                    BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                    rawResult = multiFormatReader.decodeWithState(bitmap);
                    isReDecode = false;
                }catch (Exception e){
                    isReDecode = true;
                }
            }

            if(isReDecode && isSupportVerticalCode){
                source = buildPlanarYUVLuminanceSource(data,width,height,!isScreenPortrait);
                if(source!=null){
                    try{
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        rawResult = multiFormatReader.decodeWithState(bitmap);
                    }catch (Exception e){

                    }
                }

            }

            multiFormatReader.reset();
        }

        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            LogUtils.d("Found barcode in " + (end - start) + " ms");

            BarcodeFormat barcodeFormat = rawResult.getBarcodeFormat();
            if(handler!=null && handler.isSupportAutoZoom() && barcodeFormat == BarcodeFormat.QR_CODE){

                ResultPoint[] resultPoints = rawResult.getResultPoints();
                if(resultPoints.length >= 3){
                    float distance1 = ResultPoint.distance(resultPoints[0],resultPoints[1]);
                    float distance2 = ResultPoint.distance(resultPoints[1],resultPoints[2]);
                    float distance3 = ResultPoint.distance(resultPoints[0],resultPoints[2]);
                    int maxDistance = (int)Math.max(Math.max(distance1,distance2),distance3);
                    if(handleAutoZoom(maxDistance,width)){
                        Message message = Message.obtain();
                        message.what = R.id.decode_succeeded;
                        message.obj = rawResult;
                        if(handler.isReturnBitmap()){
                            Bundle bundle = new Bundle();
                            bundleThumbnail(source, bundle);
                            message.setData(bundle);
                        }
                        handler.sendMessageDelayed(message,300);
                        return;
                    }
                }

            }

            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                if(handler.isReturnBitmap()){
                    Bundle bundle = new Bundle();
                    bundleThumbnail(source, bundle);
                    message.setData(bundle);
                }
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }

    private PlanarYUVLuminanceSource buildPlanarYUVLuminanceSource(byte[] data, int width, int height,boolean isRotate){
        PlanarYUVLuminanceSource source;
        if(isRotate){
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
            int tmp = width;
            width = height;
            height = tmp;
            source = cameraManager.buildLuminanceSource(rotatedData, width, height);
        }else{
            source = cameraManager.buildLuminanceSource(data, width, height);
        }
        return source;
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
    }

    private boolean handleAutoZoom(int length,int width){
        if(lastZoomTime > System.currentTimeMillis() - 1000){
            return true;
        }

        if(length < width/ 5){

            Camera camera = cameraManager.getOpenCamera().getCamera();
            if(camera!=null){
                Camera.Parameters params = camera.getParameters();
                if (params.isZoomSupported()) {
                    int maxZoom = params.getMaxZoom();
                    int zoom = params.getZoom();
                    params.setZoom(Math.min(zoom + maxZoom/5,maxZoom));
                    camera.setParameters(params);
                    lastZoomTime = System.currentTimeMillis();
                    return true;
                } else {
                    LogUtils.d("Zoom not supported");
                }
            }

        }

        return false;
    }

}