package com.king.zxing.app

import android.app.Activity
import android.content.Intent
import com.google.zxing.Result
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.camera.scan.analyze.Analyzer
import com.king.zxing.BarcodeCameraScanActivity
import com.king.zxing.DecodeConfig
import com.king.zxing.DecodeFormatManager
import com.king.zxing.analyze.MultiFormatAnalyzer

/**
 * 扫二维码识别示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class QRCodeScanActivity : BarcodeCameraScanActivity() {

    override fun initCameraScan(cameraScan: CameraScan<Result>) {
        super.initCameraScan(cameraScan)
        cameraScan.setPlayBeep(true)
    }

    override fun createAnalyzer(): Analyzer<Result>? {
        val decodeConfig = DecodeConfig()
            .setHints(DecodeFormatManager.QR_CODE_HINTS)
            .setFullAreaScan(false)
            .setAreaRectRatio(0.8f)
            .setAreaRectVerticalOffset(0)
            .setAreaRectHorizontalOffset(0)
        return MultiFormatAnalyzer(decodeConfig)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_qrcode_scan
    }

    override fun onScanResultCallback(result: AnalyzeResult<Result>) {
        cameraScan.setAnalyzeImage(false)
        val intent = Intent()
        intent.putExtra(CameraScan.SCAN_RESULT, result.result.text)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
