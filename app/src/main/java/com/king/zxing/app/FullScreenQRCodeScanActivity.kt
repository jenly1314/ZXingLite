package com.king.zxing.app

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.zxing.Result
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.camera.scan.analyze.Analyzer
import com.king.camera.scan.util.PointUtils
import com.king.view.viewfinderview.ViewfinderView.ViewfinderStyle
import com.king.zxing.DecodeConfig
import com.king.zxing.DecodeFormatManager
import com.king.zxing.BarcodeCameraScanActivity
import com.king.zxing.analyze.QRCodeAnalyzer

/**
 * 扫二维码全屏识别示例
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class FullScreenQRCodeScanActivity : BarcodeCameraScanActivity() {

    override fun initUI() {
        super.initUI()

        // 设置取景框样式
        viewfinderView.setViewfinderStyle(ViewfinderStyle.POPULAR)

    }


    override fun initCameraScan(cameraScan: CameraScan<Result>) {
        super.initCameraScan(cameraScan)
        // 根据需要设置CameraScan相关配置
        cameraScan.setPlayBeep(true)
    }

    override fun createAnalyzer(): Analyzer<Result>? {
        // 初始化解码配置
        val decodeConfig = DecodeConfig().apply {
            // 如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
            hints = DecodeFormatManager.QR_CODE_HINTS
            // 设置是否全区域识别，默认false
            isFullAreaScan = true
        }
        // BarcodeCameraScanActivity默认使用的MultiFormatAnalyzer，这里可以改为使用QRCodeAnalyzer
        return QRCodeAnalyzer(decodeConfig)
    }

    /**
     * 布局ID；通过覆写此方法可以自定义布局
     *
     * @return 布局ID
     */
    override fun getLayoutId(): Int {
        return super.getLayoutId()
    }

    override fun onScanResultCallback(result: AnalyzeResult<Result>) {
        // 停止分析
        cameraScan.setAnalyzeImage(false)
        // 显示结果点
        displayResultPoint(result)

        // 返回结果
        val intent = Intent()
        intent.putExtra(CameraScan.SCAN_RESULT, result.result.text)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * 显示结果点
     */
    private fun displayResultPoint(result: AnalyzeResult<Result>) {
        val frameMetadata = result.frameMetadata
        var width = frameMetadata.width
        var height = frameMetadata.height
        if (frameMetadata.rotation == 90 || frameMetadata.rotation == 270) {
            width = frameMetadata.height
            height = frameMetadata.width
        }
        val resultPoints = result.result.resultPoints
        val size = resultPoints.size
        if (size > 0) {
            var x = 0f
            var y = 0f
            resultPoints.forEach {
                x += it.x
                y += it.y
            }
            var centerX = x / size
            var centerY = y / size
            //将实际的结果中心点坐标转换成界面预览的坐标
            val point = PointUtils.transform(
                centerX.toInt(),
                centerY.toInt(),
                width,
                height,
                viewfinderView.width,
                viewfinderView.height
            )
            //显示结果点信息
            viewfinderView.showResultPoints(listOf(point))
        }
    }

}