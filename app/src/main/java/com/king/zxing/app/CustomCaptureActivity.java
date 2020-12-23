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

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.king.zxing.app.util.StatusBarUtils;


import androidx.appcompat.widget.Toolbar;

/**
 * 自定义继承CaptureActivity
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CustomCaptureActivity extends CaptureActivity {

    private boolean isContinuousScan;

    private Toast toast;

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

        isContinuousScan = getIntent().getBooleanExtra(MainActivity.KEY_IS_CONTINUOUS,false);

    }

    @Override
    public void initCameraScan() {
        super.initCameraScan();
        //获取CaptureHelper，里面有扫码相关的配置设置
        getCameraScan().setPlayBeep(false)//播放音效
                .setVibrate(true);//震动
    }

    /**
     * 扫码结果回调
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onScanResultCallback(Result result) {
        if(isContinuousScan){
            showToast(result.getText());
        }
        //如果支持连扫，返回true即可
        return isContinuousScan;
    }

    private void showToast(String text){
        if(toast == null){
            toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        }else{
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(text);
        }
        toast.show();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                onBackPressed();
                break;
        }
    }
}