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
package com.king.zxing.app;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.zxing.CaptureActivity;
import com.king.zxing.app.util.StatusBarUtils;
import com.king.zxing.camera.CameraConfigurationUtils;

import androidx.appcompat.widget.Toolbar;

/**
 * 自定义继承CaptureActivity
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CustomCaptureActivity extends CaptureActivity {

    private ImageView ivFlash;

    private boolean isContinuousScan;
    @Override
    public int getLayoutId() {
        return R.layout.custom_capture_activity;
    }

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        Toolbar toolbar = findViewById(R.id.toolbar);
        StatusBarUtils.immersiveStatusBar(this,toolbar,0.2f);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra(MainActivity.KEY_TITLE));

        ivFlash = findViewById(R.id.ivFlash);

        if(!hasTorch()){
            ivFlash.setVisibility(View.GONE);
        }

        isContinuousScan = getIntent().getBooleanExtra(MainActivity.KEY_IS_CONTINUOUS,false);
        //获取CaptureHelper，里面有扫码相关的配置设置
        getCaptureHelper().playBeep(false)//播放音效
                .vibrate(true)//震动
//                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .continuousScan(isContinuousScan);//是否连扫
    }

    /**
     * 开启或关闭闪光灯（手电筒）
     * @param on {@code true}表示开启，{@code false}表示关闭
     */
    public void setTorch(boolean on){
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        CameraConfigurationUtils.setTorch(parameters,on);
        camera.setParameters(parameters);

    }

    /**
     * 检测是否支持闪光灯（手电筒）
     * @return
     */
    public boolean hasTorch(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    /**
     * 扫码结果回调
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onResultCallback(String result) {
        if(isContinuousScan){//连续扫码时，直接弹出结果
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        }

        return super.onResultCallback(result);
    }


    private void clickFlash(View v){
        boolean isSelected = v.isSelected();
        setTorch(!isSelected);
        v.setSelected(!isSelected);
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
