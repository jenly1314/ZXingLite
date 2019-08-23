package com.king.zxing.app;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.king.zxing.CaptureHelper;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.king.zxing.app.util.StatusBarUtils;

/**
 * 自定义扫码：当直接使用CaptureActivity
 * 自定义扫码，切记自定义扫码需在{@link Activity}或者{@link Fragment}相对应的生命周期里面调用{@link #mCaptureHelper}对应的生命周期
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CustomActivity extends AppCompatActivity implements OnCaptureCallback {

    private boolean isContinuousScan;

    private CaptureHelper mCaptureHelper;

    private SurfaceView surfaceView;

    private ViewfinderView viewfinderView;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.custom_activity);

        initUI();
    }

    private void initUI(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        StatusBarUtils.immersiveStatusBar(this,toolbar,0.2f);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra(MainActivity.KEY_TITLE));


        surfaceView = findViewById(R.id.surfaceView);
        viewfinderView = findViewById(R.id.viewfinderView);

        isContinuousScan = getIntent().getBooleanExtra(MainActivity.KEY_IS_CONTINUOUS,false);

        mCaptureHelper = new CaptureHelper(this,surfaceView,viewfinderView);
        mCaptureHelper.setOnCaptureCallback(this);
        mCaptureHelper.onCreate();
        mCaptureHelper.vibrate(true)
                .fullScreenScan(true)//全屏扫码
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .continuousScan(isContinuousScan);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 关闭闪光灯（手电筒）
     */
    private void offFlash(){
        Camera camera = mCaptureHelper.getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    /**
     * 开启闪光灯（手电筒）
     */
    public void openFlash(){
        Camera camera = mCaptureHelper.getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
    }


    /**
     * 扫码结果回调
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onResultCallback(String result) {
        if(isContinuousScan){
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void clickFlash(View v){
        if(v.isSelected()){
            offFlash();
            v.setSelected(false);
        }else{
            openFlash();
            v.setSelected(true);
        }

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                onBackPressed();
                break;
            case R.id.ivFlash:
                clickFlash(v);
                break;
        }
    }
}
