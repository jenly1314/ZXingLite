package com.king.zxing.app

import android.widget.Toast
import com.google.zxing.Result
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.camera.scan.analyze.Analyzer
import com.king.zxing.DecodeConfig
import com.king.zxing.BarcodeCameraScanActivity
import com.king.zxing.analyze.MultiFormatAnalyzer

/**
 * 连续扫码（识别多种格式）示例
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class MultiFormatScanActivity : BarcodeCameraScanActivity() {

    private var toast: Toast? = null

    override fun initCameraScan(cameraScan: CameraScan<Result>) {
        super.initCameraScan(cameraScan)
        // 根据需要设置CameraScan相关配置
        cameraScan.setPlayBeep(true)
    }

    override fun createAnalyzer(): Analyzer<Result>? {
        // 初始化解码配置
        val decodeConfig = DecodeConfig().apply {
            // 设置是否支持扫垂直的条码
            isSupportVerticalCode = true
            // 设置是否支持识别反色码，黑白颜色反转
            isSupportLuminanceInvert = true
        }
        // 多格式分析器（支持的条码格式主要包含：一维码和二维码）
        return MultiFormatAnalyzer(decodeConfig)
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
        // 处理扫码结果相关逻辑（此处弹Toast只是为了演示）
        showToast(result.result.text)
        // 继续分析
        cameraScan.setAnalyzeImage(true)
    }

    private fun showToast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
    }
}