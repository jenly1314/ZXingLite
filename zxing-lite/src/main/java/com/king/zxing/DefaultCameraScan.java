package com.king.zxing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.detector.MathUtils;
import com.king.zxing.analyze.Analyzer;
import com.king.zxing.analyze.MultiFormatAnalyzer;
import com.king.zxing.config.CameraConfig;
import com.king.zxing.manager.AmbientLightManager;
import com.king.zxing.manager.BeepManager;
import com.king.zxing.util.LogUtils;

import java.util.concurrent.Executors;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DefaultCameraScan extends CameraScan {

    /**
     * Defines the maximum duration in milliseconds between a touch pad
     * touch and release for a given touch to be considered a tap (click) as
     * opposed to a hover movement gesture.
     */
    private static final int HOVER_TAP_TIMEOUT = 150;

    /**
     * Defines the maximum distance in pixels that a touch pad touch can move
     * before being released for it to be considered a tap (click) as opposed
     * to a hover movement gesture.
     */
    private static final int HOVER_TAP_SLOP = 20;

    private FragmentActivity mFragmentActivity;
    private Context mContext;
    private LifecycleOwner mLifecycleOwner;
    private PreviewView mPreviewView;

    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private Camera mCamera;

    private CameraConfig mCameraConfig;
    private Analyzer mAnalyzer;

    /**
     * 是否分析
     */
    private volatile boolean isAnalyze = true;

    /**
     * 是否已经分析出结果
     */
    private volatile boolean isAnalyzeResult;

    private View flashlightView;

    private MutableLiveData<Result> mResultLiveData;

    private OnScanResultCallback mOnScanResultCallback;

    private BeepManager mBeepManager;
    private AmbientLightManager mAmbientLightManager;

    private int mOrientation;
    private int mScreenWidth;
    private int mScreenHeight;
    private long mLastAutoZoomTime;
    private long mLastHoveTapTime;
    private boolean isClickTap;
    private float mDownX;
    private float mDownY;

    public DefaultCameraScan(@NonNull FragmentActivity activity,@NonNull  PreviewView previewView){
        this.mFragmentActivity = activity;
        this.mLifecycleOwner = activity;
        this.mContext = activity;
        this.mPreviewView = previewView;
        initData();
    }

    public DefaultCameraScan(@NonNull Fragment fragment,@NonNull PreviewView previewView){
        this.mFragmentActivity = fragment.getActivity();
        this.mLifecycleOwner = fragment;
        this.mContext = fragment.getContext();
        this.mPreviewView = previewView;
        initData();
    }

    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            if(mCamera != null){
                float ratio = mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio();
                zoomTo(ratio * scale);
            }
            return true;
        }

    };

    private void initData(){
        mResultLiveData = new MutableLiveData<>();
        mResultLiveData.observe(mLifecycleOwner, result -> {
            if(result != null){
                handleAnalyzeResult(result);
            }else if(mOnScanResultCallback != null){
                mOnScanResultCallback.onScanResultFailure();
            }
        });

        mOrientation = mContext.getResources().getConfiguration().orientation;

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(mContext, mOnScaleGestureListener);
        mPreviewView.setOnTouchListener((v, event) -> {
            handlePreviewViewClickTap(event);
            if(isNeedTouchZoom()){
                return scaleGestureDetector.onTouchEvent(event);
            }
            return false;
        });

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;

        LogUtils.d(String.format("displayMetrics:%dx%d",mScreenWidth,mScreenHeight));

        mBeepManager = new BeepManager(mContext);
        mAmbientLightManager = new AmbientLightManager(mContext);
        if(mAmbientLightManager != null){
            mAmbientLightManager.register();
            mAmbientLightManager.setOnLightSensorEventListener((dark, lightLux) -> {
                if(flashlightView != null){
                    if(dark){
                        if(flashlightView.getVisibility() != View.VISIBLE){
                            flashlightView.setVisibility(View.VISIBLE);
                            flashlightView.setSelected(isTorchEnabled());
                        }
                    }else if(flashlightView.getVisibility() == View.VISIBLE && !isTorchEnabled()){
                        flashlightView.setVisibility(View.INVISIBLE);
                        flashlightView.setSelected(false);
                    }

                }
            });
        }
    }

    private void handlePreviewViewClickTap(MotionEvent event){
        if(event.getPointerCount() == 1){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isClickTap = true;
                    mDownX = event.getX();
                    mDownY = event.getY();
                    mLastHoveTapTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isClickTap = MathUtils.distance(mDownX,mDownY,event.getX(),event.getY()) < HOVER_TAP_SLOP;
                    break;
                case MotionEvent.ACTION_UP:
                    if(isClickTap && mLastHoveTapTime + HOVER_TAP_TIMEOUT > System.currentTimeMillis()){
                        startFocusAndMetering(event.getX(),event.getY());
                    }
                    break;
            }
        }
    }

    private void startFocusAndMetering(float x, float y){
        if(mCamera != null){
            LogUtils.d("startFocusAndMetering:" + x + "," + y);
            MeteringPoint point = mPreviewView.getMeteringPointFactory().createPoint(x,y);
            mCamera.getCameraControl().startFocusAndMetering(new FocusMeteringAction.Builder(point).build());
        }
    }



    private void initConfig(){
        if(mCameraConfig == null){
            mCameraConfig = new CameraConfig();
        }
        if(mAnalyzer == null){
            mAnalyzer = new MultiFormatAnalyzer();
        }
    }


    @Override
    public CameraScan setCameraConfig(CameraConfig cameraConfig) {
        if(cameraConfig != null){
            this.mCameraConfig = cameraConfig;
        }
        return this;
    }

    @Override
    public void startCamera(){
        initConfig();
        mCameraProviderFuture = ProcessCameraProvider.getInstance(mContext);
        mCameraProviderFuture.addListener(() -> {

            try{
                Preview preview = mCameraConfig.options(new Preview.Builder());

                //相机选择器
                CameraSelector cameraSelector = mCameraConfig.options(new CameraSelector.Builder());
                //设置SurfaceProvider
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

                //图像分析
                ImageAnalysis imageAnalysis = mCameraConfig.options(new ImageAnalysis.Builder()
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST));
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), image -> {
                    if(isAnalyze && !isAnalyzeResult && mAnalyzer != null){
                        Result result = mAnalyzer.analyze(image,mOrientation);
                        mResultLiveData.postValue(result);
                    }
                    image.close();
                });
                if(mCamera != null){
                    mCameraProviderFuture.get().unbindAll();
                }
                //绑定到生命周期
                mCamera = mCameraProviderFuture.get().bindToLifecycle(mLifecycleOwner, cameraSelector, preview, imageAnalysis);
            }catch (Exception e){
                LogUtils.e(e);
            }

        },ContextCompat.getMainExecutor(mContext));
    }

    /**
     * 处理分析结果
     * @param result
     */
    private synchronized void handleAnalyzeResult(Result result){

        if(isAnalyzeResult || !isAnalyze){
            return;
        }
        isAnalyzeResult = true;
        if(mBeepManager != null){
            mBeepManager.playBeepSoundAndVibrate();
        }

        if(result.getBarcodeFormat() == BarcodeFormat.QR_CODE && isNeedAutoZoom() && mLastAutoZoomTime + 100 < System.currentTimeMillis()){
            ResultPoint[] points = result.getResultPoints();
            if(points != null && points.length >= 2){
                float distance1 = ResultPoint.distance(points[0],points[1]);
                float maxDistance = distance1;
                if(points.length >= 3){
                    float distance2 = ResultPoint.distance(points[1],points[2]);
                    float distance3 = ResultPoint.distance(points[0],points[2]);
                    maxDistance = Math.max(Math.max(distance1,distance2),distance3);
                }
                if(handleAutoZoom((int)maxDistance,result)){
                    return;
                }
            }
        }

        scanResultCallback(result);
    }

    private boolean handleAutoZoom(int distance,Result result){
        int size = Math.min(mScreenWidth,mScreenHeight);
        if(distance * 4 < size){
            mLastAutoZoomTime = System.currentTimeMillis();
            zoomIn();
            scanResultCallback(result);
            return true;
        }
        return false;
    }

    private void scanResultCallback(Result result){
        if(mOnScanResultCallback != null && mOnScanResultCallback.onScanResultCallback(result)){
            /*
             * 如果拦截了结果，则重置分析结果状态，并当isAnalyze为true时，默认会继续分析图像（也就是连扫）。
             * 如果只是想拦截扫码结果回调，并不想继续分析图像（不想连扫），请在拦截扫码逻辑处通过调用
             * setAnalyzeImage(false)，因为setAnalyzeImage方法能动态控制是否继续分析图像。
             */
            isAnalyzeResult = false;
            return;
        }

        if(mFragmentActivity != null){
            Intent intent = new Intent();
            intent.putExtra(SCAN_RESULT,result.getText());
            mFragmentActivity.setResult(Activity.RESULT_OK,intent);
            mFragmentActivity.finish();
        }
    }


    @Override
    public void stopCamera(){
        if(mCameraProviderFuture != null){
            try {
                mCameraProviderFuture.get().unbindAll();
            }catch (Exception e){
                LogUtils.e(e);
            }
        }
    }

    @Override
    public CameraScan setAnalyzeImage(boolean analyze) {
        isAnalyze = analyze;
        return this;
    }

    @Override
    public CameraScan setAnalyzer(Analyzer analyzer) {
        mAnalyzer = analyzer;
        return this;
    }

    @Override
    public void zoomIn(){
        if(mCamera != null){
            float ratio = mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio() + 0.1f;
            float maxRatio = mCamera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio();
            if(ratio <= maxRatio){
                mCamera.getCameraControl().setZoomRatio(ratio);
            }
        }
    }

    @Override
    public void zoomOut(){
        if(mCamera != null){
            float ratio = mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio() - 0.1f;
            float minRatio = mCamera.getCameraInfo().getZoomState().getValue().getMinZoomRatio();
            if(ratio >= minRatio){
                mCamera.getCameraControl().setZoomRatio(ratio);
            }
        }
    }


    @Override
    public void zoomTo(float ratio) {
        if(mCamera != null){
            ZoomState zoomState = mCamera.getCameraInfo().getZoomState().getValue();
            float maxRatio = zoomState.getMaxZoomRatio();
            float minRatio = zoomState.getMinZoomRatio();
            float zoom = Math.max(Math.min(ratio,maxRatio),minRatio);
            mCamera.getCameraControl().setZoomRatio(zoom);
        }
    }

    @Override
    public void lineZoomIn() {
        if(mCamera != null){
            float zoom = mCamera.getCameraInfo().getZoomState().getValue().getLinearZoom() + 0.1f;
            if(zoom <= 1f){
                mCamera.getCameraControl().setLinearZoom(zoom);
            }
        }
    }

    @Override
    public void lineZoomOut() {
        if(mCamera != null){
            float zoom = mCamera.getCameraInfo().getZoomState().getValue().getLinearZoom() - 0.1f;
            if(zoom >= 0f){
                mCamera.getCameraControl().setLinearZoom(zoom);
            }
        }
    }

    @Override
    public void lineZoomTo(@FloatRange(from = 0.0,to = 1.0) float linearZoom) {
        if(mCamera != null){
            mCamera.getCameraControl().setLinearZoom(linearZoom);
        }
    }

    @Override
    public void enableTorch(boolean torch) {
        if(mCamera != null && hasFlashUnit()){
            mCamera.getCameraControl().enableTorch(torch);
        }
    }

    @Override
    public boolean isTorchEnabled() {
        if(mCamera != null){
            return mCamera.getCameraInfo().getTorchState().getValue() == TorchState.ON;
        }
        return false;
    }

    /**
     * 是否支持闪光灯
     * @return
     */
    @Override
    public boolean hasFlashUnit(){
        if(mCamera != null){
            return mCamera.getCameraInfo().hasFlashUnit();
        }
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public CameraScan setVibrate(boolean vibrate) {
        if(mBeepManager != null){
            mBeepManager.setVibrate(vibrate);
        }
        return this;
    }

    @Override
    public CameraScan setPlayBeep(boolean playBeep) {
        if(mBeepManager != null){
            mBeepManager.setPlayBeep(playBeep);
        }
        return this;
    }

    @Override
    public CameraScan setOnScanResultCallback(OnScanResultCallback callback) {
        this.mOnScanResultCallback = callback;
        return this;
    }

    @Nullable
    @Override
    public Camera getCamera(){
        return mCamera;
    }


    @Override
    public void release() {
        isAnalyze = false;
        flashlightView = null;
        if(mAmbientLightManager != null){
            mAmbientLightManager.unregister();
        }
        if(mBeepManager != null){
            mBeepManager.close();
        }
        stopCamera();
    }

    @Override
    public CameraScan bindFlashlightView(@Nullable View v) {
        flashlightView = v;
        if(mAmbientLightManager != null){
            mAmbientLightManager.setLightSensorEnabled(v != null);
        }
        return this;
    }

    public CameraScan setDarkLightLux(float lightLux){
        if(mAmbientLightManager != null){
            mAmbientLightManager.setDarkLightLux(lightLux);
        }
        return this;
    }

    public CameraScan setBrightLightLux(float lightLux){
        if(mAmbientLightManager != null){
            mAmbientLightManager.setBrightLightLux(lightLux);
        }
        return this;
    }

}
