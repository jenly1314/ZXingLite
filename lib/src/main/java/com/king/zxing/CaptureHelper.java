/*
 * Copyright (C) 2019 Jenly Yu
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
package com.king.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.camera.CameraManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CaptureHelper implements CaptureLifecycle,CaptureTouchEvent,CaptureManager  {

    public static final String TAG = CaptureHelper.class.getSimpleName();

    private Activity activity;

    private CaptureHandler captureHandler;
    private OnCaptureListener onCaptureListener;

    private CameraManager cameraManager;

    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;


    private ViewfinderView viewfinderView;
    private SurfaceHolder surfaceHolder;
    private SurfaceHolder.Callback callback;

    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType,?> decodeHints;
    private String characterSet;

    private boolean hasSurface;
    /**
     * 默认触控误差值
     */
    private static final int DEVIATION = 6;
    /**
     * 是否支持缩放（变焦），默认支持
     */
    private boolean isSupportZoom = true;
    private float oldDistance;

    /**
     * 是否支持自动缩放（变焦），默认支持
     */
    private boolean isSupportAutoZoom = true;

    /**
     * 是否支持连扫，默认不支持
     */
    private boolean isContinuousScan = false;
    /**
     * 连扫时，是否自动重置预览和解码器，默认自动重置
     */
    private boolean isAutoRestartPreviewAndDecode = true;
    /**
     * 是否播放音效
     */
    private boolean isPlayBeep;
    /**
     * 是否震动
     */
    private boolean isVibrate;

    /**
     * 是否支持垂直的条形码
     */
    private boolean isSupportVerticalCode;

    /**
     * 是否返回扫码原图
     */
    private boolean isReturnBitmap;

    /**
     * 是否支持全屏扫码识别
     */
    private boolean isFullScreenScan;

    private OnCaptureCallback onCaptureCallback;


    public CaptureHelper(Fragment fragment, SurfaceView surfaceView, ViewfinderView viewfinderView){
        this(fragment.getActivity(),surfaceView,viewfinderView);

    }

    public CaptureHelper(Activity activity,SurfaceView surfaceView,ViewfinderView viewfinderView){
        this.activity = activity;
        this.viewfinderView = viewfinderView;
        surfaceHolder = surfaceView.getHolder();
        hasSurface = false;

    }

    @Override
    public void onCreate(){
        inactivityTimer = new InactivityTimer(activity);
        beepManager = new BeepManager(activity);
        ambientLightManager = new AmbientLightManager(activity);

        cameraManager = new CameraManager(activity);
        cameraManager.setFullScreenScan(isFullScreenScan);
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (holder == null) {
                    Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
                }
                if (!hasSurface) {
                    hasSurface = true;
                    initCamera(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                hasSurface = false;
            }
        };

        onCaptureListener =  new OnCaptureListener() {

            @Override
            public void onHandleDecode(Result result, Bitmap barcode, float scaleFactor) {
                inactivityTimer.onActivity();
                beepManager.playBeepSoundAndVibrate();

                onResult(result);
            }

        };
        //设置是否播放音效和震动
        beepManager.setPlayBeep(isPlayBeep);
        beepManager.setVibrate(isVibrate);
    }


    /**
     * {@link Activity#onResume()}
     */
    @Override
    public void onResume(){
        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        surfaceHolder.addCallback(callback);

        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(callback);
        }
    }

    /**
     * {@link Activity#onPause()}
     */
    @Override
    public void onPause(){
        if (captureHandler != null) {
            captureHandler.quitSynchronously();
            captureHandler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            surfaceHolder.removeCallback(callback);
        }
    }

    /**
     * {@link Activity#onDestroy()}
     */
    @Override
    public void onDestroy(){
        inactivityTimer.shutdown();
    }

    /**
     * 支持缩放时，须在{@link Activity#onTouchEvent(MotionEvent)}调用此方法
     * @param event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(isSupportZoom && cameraManager.isOpen()){
            Camera camera = cameraManager.getOpenCamera().getCamera();
            if(camera ==null){
                return false;
            }
            if(event.getPointerCount() > 1) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {//多点触控
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDistance = calcFingerSpacing(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newDistance = calcFingerSpacing(event);

                        if (newDistance > oldDistance + DEVIATION) {//
                            handleZoom(true, camera);
                        } else if (newDistance < oldDistance - DEVIATION) {
                            handleZoom(false, camera);
                        }
                        oldDistance = newDistance;
                        break;
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 初始化Camera
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (captureHandler == null) {
                captureHandler = new CaptureHandler(activity,viewfinderView,onCaptureListener, decodeFormats, decodeHints, characterSet, cameraManager);
                captureHandler.setSupportVerticalCode(isSupportVerticalCode);
                captureHandler.setReturnBitmap(isReturnBitmap);
                captureHandler.setSupportAutoZoom(isSupportAutoZoom);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    /**
     * 处理变焦缩放
     * @param isZoomIn
     * @param camera
     */
    private void handleZoom(boolean isZoomIn, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            camera.setParameters(params);
        } else {
            Log.i(TAG, "zoom not supported");
        }
    }

    /**
     * 聚焦
     * @param event
     * @param camera
     */
    private void focusOnTouch(MotionEvent event,Camera camera) {

        Camera.Parameters params = camera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();

        Rect focusRect = calcTapArea(event.getRawX(), event.getRawY(), 1f,previewSize);
        Rect meteringRect = calcTapArea(event.getRawX(), event.getRawY(), 1.5f,previewSize);
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 600));
            parameters.setFocusAreas(focusAreas);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 600));
            parameters.setMeteringAreas(meteringAreas);
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });

    }


    /**
     * 计算两指间距离
     * @param event
     * @return
     */
    private float calcFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算对焦区域
     * @param x
     * @param y
     * @param coefficient
     * @param previewSize
     * @return
     */
    private Rect calcTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 200;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) ((x / previewSize.width) * 2000 - 1000);
        int centerY = (int) ((y / previewSize.height) * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top),
                Math.round(rectF.right), Math.round(rectF.bottom));
    }

    /**
     *
     * @param x
     * @param min
     * @param max
     * @return
     */
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    /**
     * 重新启动扫码和解码器
     */
    public void restartPreviewAndDecode(){
        if(captureHandler!=null){
            captureHandler.restartPreviewAndDecode();
        }
    }

    /**
     * 接收扫码结果，想支持连扫时，可将{@link #continuousScan(boolean)}设置为{@code true}
     * 如果{@link #isContinuousScan}支持连扫，则默认重启扫码和解码器；当连扫逻辑太复杂时，
     * 请将{@link #autoRestartPreviewAndDecode(boolean)}设置为{@code false}，并手动调用{@link #restartPreviewAndDecode()}
     * @param result 扫码结果
     */
    public void onResult(Result result){
        String text = result.getText();
        if(isContinuousScan){
            if(onCaptureCallback!=null){
                onCaptureCallback.onResultCallback(text);
            }
            if(isAutoRestartPreviewAndDecode){
                restartPreviewAndDecode();
            }
        }else{
            //如果设置了回调，并且onCallback返回为true，则表示拦截
            if(onCaptureCallback!=null && onCaptureCallback.onResultCallback(text)){
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(Intents.Scan.RESULT,text);
            activity.setResult(Activity.RESULT_OK,intent);
            activity.finish();
        }
    }

    /**
     * 设置是否连续扫码，如果想支持连续扫码，则将此方法返回{@code true}并重写{@link #onResult(Result)}
     */
    public CaptureHelper continuousScan(boolean isContinuousScan){
        this.isContinuousScan = isContinuousScan;
        return this;
    }


    /**
     * 设置是否自动重启扫码和解码器，当支持连扫时才起作用。
     * @return 默认返回 true
     */
    public CaptureHelper autoRestartPreviewAndDecode(boolean isAutoRestartPreviewAndDecode){
        this.isAutoRestartPreviewAndDecode = isAutoRestartPreviewAndDecode;
        return this;
    }


    /**
     * 设置是否播放音效
     * @return
     */
    public CaptureHelper playBeep(boolean playBeep){
        this.isPlayBeep = playBeep;
        if(beepManager!=null){
            beepManager.setPlayBeep(playBeep);
        }
        return this;
    }

    /**
     * 设置是否震动
     * @return
     */
    public CaptureHelper vibrate(boolean vibrate){
        this.isVibrate = vibrate;
        if(beepManager!=null){
            beepManager.setVibrate(vibrate);
        }
        return this;
    }


    /**
     * 设置是否支持缩放
     * @param supportZoom
     * @return
     */
    public CaptureHelper supportZoom(boolean supportZoom) {
        isSupportZoom = supportZoom;
        return this;
    }

    /**
     * 设置支持的解码一/二维码格式，默认常规的码都支持
     * @param decodeFormats
     * @return
     */
    public CaptureHelper decodeFormats(Collection<BarcodeFormat> decodeFormats) {
        this.decodeFormats = decodeFormats;
        return this;
    }

    /**
     * {@link DecodeHintType}
     * @param decodeHints
     * @return
     */
    public CaptureHelper decodeHints(Map<DecodeHintType,?> decodeHints) {
        this.decodeHints = decodeHints;
        return this;
    }

    /**
     *  设置解码时编码字符集
     * @param characterSet
     * @return
     */
    public CaptureHelper characterSet(String characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    /**
     * 设置是否支持扫垂直的条码
     * @param supportVerticalCode 默认为false，想要增强扫条码识别度时可使用，相应的会增加性能消耗。
     * @return
     */
    public CaptureHelper supportVerticalCode(boolean supportVerticalCode) {
        this.isSupportVerticalCode = supportVerticalCode;
        if(captureHandler!=null){
            captureHandler.setSupportVerticalCode(isSupportVerticalCode);
        }
        return this;
    }

    /**
     * 设置返回扫码原图
     * @param returnBitmap 默认为false，当返回true表示扫码就结果会返回扫码原图，相应的会增加性能消耗。
     * @return
     */
    public CaptureHelper returnBitmap(boolean returnBitmap) {
        isReturnBitmap = returnBitmap;
        if(captureHandler!=null){
            captureHandler.setReturnBitmap(isReturnBitmap);
        }
        return this;
    }


    /**
     * 设置是否支持自动缩放
     * @param supportAutoZoom
     * @return
     */
    public CaptureHelper supportAutoZoom(boolean supportAutoZoom) {
        isSupportAutoZoom = supportAutoZoom;
        if(captureHandler!=null){
            captureHandler.setSupportAutoZoom(isSupportAutoZoom);
        }
        return this;
    }

    /**
     * 设置是否支持全屏扫码识别
     * @param fullScreenScan
     * @return
     */
    public CaptureHelper fullScreenScan(boolean fullScreenScan) {
        isFullScreenScan = fullScreenScan;
        if(cameraManager!=null){
            cameraManager.setFullScreenScan(isFullScreenScan);
        }
        return this;
    }


    /**
     * 设置扫码回调
     * @param callback
     * @return
     */
    public CaptureHelper setOnCaptureCallback(OnCaptureCallback callback) {
        this.onCaptureCallback = callback;
        return this;
    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public BeepManager getBeepManager() {
        return beepManager;
    }

    @Override
    public AmbientLightManager getAmbientLightManager() {
        return ambientLightManager;
    }

    @Override
    public InactivityTimer getInactivityTimer() {
        return inactivityTimer;
    }
}
