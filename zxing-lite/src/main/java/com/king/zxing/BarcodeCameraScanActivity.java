package com.king.zxing;

import android.view.View;

import com.google.zxing.Result;
import com.king.camera.scan.BaseCameraScanActivity;
import com.king.camera.scan.analyze.Analyzer;
import com.king.view.viewfinderview.ViewfinderView;
import com.king.zxing.analyze.MultiFormatAnalyzer;

import androidx.annotation.Nullable;

/**
 * 基于zxing实现的扫码识别 - 相机扫描基类
 * <p>
 * 通过继承 {@link BarcodeCameraScanActivity}或{@link BarcodeCameraScanFragment}可快速实现扫码识别
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class BarcodeCameraScanActivity extends BaseCameraScanActivity<Result> {

    protected ViewfinderView viewfinderView;

    @Override
    public void initUI() {
        int viewfinderViewId = getViewfinderViewId();
        if (viewfinderViewId != View.NO_ID && viewfinderViewId != 0) {
            viewfinderView = findViewById(viewfinderViewId);
        }
        super.initUI();
    }

    @Nullable
    @Override
    public Analyzer<Result> createAnalyzer() {
        return new MultiFormatAnalyzer();
    }

    /**
     * 布局ID；通过覆写此方法可以自定义布局
     *
     * @return 布局ID
     */
    @Override
    public int getLayoutId() {
        return R.layout.zxl_camera_scan;
    }

    /**
     * {@link #viewfinderView} 的 ID
     *
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回{@link View#NO_ID}
     */

    public int getViewfinderViewId() {
        return R.id.viewfinderView;
    }

}
