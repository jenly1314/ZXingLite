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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CaptureFragment extends Fragment implements CameraScan.OnScanResultCallback {

    private View mRootView;

    protected PreviewView previewView;
    protected ViewfinderView viewfinderView;
    protected View ivFlashlight;

    private CameraScan mCameraScan;

    public static CaptureFragment newInstance() {

        Bundle args = new Bundle();

        CaptureFragment fragment = new CaptureFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if(isContentView(layoutId)){
            mRootView = createRootView(inflater,container);
        }
        initUI();
        return mRootView;
    }

    /**
     * 初始化
     */
    public void initUI(){
        previewView = mRootView.findViewById(getPreviewViewId());
        int viewfinderViewId = getViewfinderViewId();
        if(viewfinderViewId != 0){
            viewfinderView = mRootView.findViewById(viewfinderViewId);
        }
        int ivFlashlightId = getFlashlightId();
        if(ivFlashlightId != 0){
            ivFlashlight = mRootView.findViewById(ivFlashlightId);
            if(ivFlashlight != null){
                ivFlashlight.setOnClickListener(v -> toggleTorch());
            }
        }
        initCameraScan();
        startCamera();
    }

    public void initCameraScan(){
        mCameraScan = new DefaultCameraScan(this,previewView);
        mCameraScan.setOnScanResultCallback(this);
    }

    public void startCamera(){
        if(mCameraScan != null){
            mCameraScan.startCamera();
        }
    }

    private void releaseCamera(){
        if(mCameraScan != null){
            mCameraScan.release();
        }
    }

    protected void toggleTorch(){
        if(mCameraScan != null){
            boolean isTorch = mCameraScan.isTorchEnabled();
            mCameraScan.enableTorch(!isTorch);
            if(ivFlashlight != null){
                ivFlashlight.setSelected(!isTorch);
            }
        }
    }

    @Override
    public void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }

    /**
     * 返回true时会自动初始化{@link #createRootView(LayoutInflater, ViewGroup)}，返回为false是需自己去初始化{@link #createRootView(LayoutInflater, ViewGroup)}
     * @param layoutId
     * @return 默认返回true
     */
    public boolean isContentView(@LayoutRes int layoutId){
        return true;
    }

    /**
     * 创建{@link #mRootView}
     * @param inflater
     * @param container
     * @return
     */
    @NonNull
    public View createRootView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(getLayoutId(),container,false);
    }

    /**
     * 布局id
     * @return
     */
    public int getLayoutId(){
        return R.layout.zxl_capture;
    }

    /**
     * {@link #viewfinderView} 的 ID
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回0
     */
    public int getViewfinderViewId(){
        return R.id.viewfinderView;
    }


    /**
     * 预览界面{@link #previewView} 的ID
     * @return
     */
    public int getPreviewViewId(){
        return R.id.previewView;
    }

    /**
     * 获取 {@link #ivFlashlight} 的ID
     * @return  默认返回{@code R.id.ivFlashlight}, 如果不需要手电筒按钮可以返回0
     */
    public int getFlashlightId(){
        return R.id.ivFlashlight;
    }

    /**
     * Get {@link CameraScan}
     * @return {@link #mCameraScan}
     */
    public CameraScan getCameraScan(){
        return mCameraScan;
    }

    /**
     * 接收扫码结果回调
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
     */
    @Override
    public boolean onScanResultCallback(Result result) {
        return false;
    }

    //--------------------------------------------

    public View getRootView() {
        return mRootView;
    }


}
