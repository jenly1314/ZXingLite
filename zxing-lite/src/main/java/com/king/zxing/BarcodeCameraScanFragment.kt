package com.king.zxing

import android.view.View
import androidx.annotation.IdRes
import com.google.zxing.Result
import com.king.camera.scan.BaseCameraScanFragment
import com.king.camera.scan.analyze.Analyzer
import com.king.view.viewfinderview.ViewfinderView
import com.king.zxing.analyze.MultiFormatAnalyzer

/**
 * 基于zxing实现的扫码识别 - 相机扫描基类
 * <p>
 * 通过继承 [BarcodeCameraScanActivity]或[BarcodeCameraScanFragment]可快速实现扫码识别
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
abstract class BarcodeCameraScanFragment : BaseCameraScanFragment<Result>() {

    protected lateinit var viewfinderView: ViewfinderView

    override fun initUI() {
        val viewfinderViewId = getViewfinderViewId()
        if (viewfinderViewId != View.NO_ID && viewfinderViewId != 0) {
            viewfinderView = getRootView().findViewById(viewfinderViewId)
        }
        super.initUI()
    }

    override fun createAnalyzer(): Analyzer<Result>? {
        return MultiFormatAnalyzer()
    }

    override fun getLayoutId(): Int {
        return R.layout.zxl_camera_scan
    }

    @IdRes
    open fun getViewfinderViewId(): Int {
        return R.id.viewfinderView
    }
}
