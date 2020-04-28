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
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.king.zxing.camera.CameraManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CaptureFragment extends Fragment implements OnCaptureCallback {

    public static final String KEY_RESULT = Intents.Scan.RESULT;

    private View mRootView;

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private View ivTorch;

    private CaptureHelper mCaptureHelper;

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
            mRootView = inflater.inflate(getLayoutId(),container,false);
        }
        initUI();
        return mRootView;
    }

    /**
     * 初始化
     */
    public void initUI(){
        surfaceView = mRootView.findViewById(getSurfaceViewId());
        int viewfinderViewId = getViewfinderViewId();
        if(viewfinderViewId != 0){
            viewfinderView = mRootView.findViewById(viewfinderViewId);
        }
        int ivTorchId = getIvTorchId();
        if(ivTorchId != 0){
            ivTorch = mRootView.findViewById(ivTorchId);
            ivTorch.setVisibility(View.INVISIBLE);
        }
        initCaptureHelper();
    }

    public void initCaptureHelper(){
        mCaptureHelper = new CaptureHelper(this,surfaceView,viewfinderView,ivTorch);
        mCaptureHelper.setOnCaptureCallback(this);
    }

    /**
     * 返回true时会自动初始化{@link #mRootView}，返回为false时需自己去通过{@link #setRootView(View)}初始化{@link #mRootView}
     * @param layoutId
     * @return 默认返回true
     */
    public boolean isContentView(@LayoutRes int layoutId){
        return true;
    }

    /**
     * 布局id
     * @return
     */
    public int getLayoutId(){
        return R.layout.zxl_capture;
    }

    /**
     * {@link ViewfinderView} 的 id
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回0
     */
    public int getViewfinderViewId(){
        return R.id.viewfinderView;
    }

    /**
     * 预览界面{@link #surfaceView} 的id
     * @return
     */
    public int getSurfaceViewId(){
        return R.id.surfaceView;
    }

    /**
     * 获取 {@link #ivTorch} 的ID
     * @return  默认返回{@code R.id.ivTorch}, 如果不需要手电筒按钮可以返回0
     */
    public int getIvTorchId(){
        return R.id.ivTorch;
    }

    /**
     * Get {@link CaptureHelper}
     * @return {@link #mCaptureHelper}
     */
    public CaptureHelper getCaptureHelper(){
        return mCaptureHelper;
    }

    /**
     * Get {@link CameraManager} use {@link #getCaptureHelper()#getCameraManager()}
     * @return {@link #mCaptureHelper#getCameraManager()}
     */
    @Deprecated
    public CameraManager getCameraManager(){
        return mCaptureHelper.getCameraManager();
    }

    //--------------------------------------------

    public View getRootView() {
        return mRootView;
    }

    public void setRootView(View rootView) {
        this.mRootView = rootView;
    }


    //--------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCaptureHelper.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    /**
     * 接收扫码结果回调
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
     */
    @Override
    public boolean onResultCallback(String result) {
        return false;
    }

}
