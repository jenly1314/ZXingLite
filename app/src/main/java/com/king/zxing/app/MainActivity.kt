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
package com.king.zxing.app

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.king.camera.scan.CameraScan
import com.king.logx.LogX
import com.king.zxing.util.CodeUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 扫码示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class MainActivity : AppCompatActivity() {

    private var toast: Toast? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var scanLauncher: ActivityResultLauncher<Intent>
    private lateinit var photoPickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val text = CameraScan.parseScanResult(result.data)
                showToast(text)
            }
        }

        photoPickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ::parsePhoto
        )
    }

    private fun showToast(text: String?) {
        toast?.cancel()
        toast = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun parsePhoto(uri: Uri?) {
        if (uri == null) {
            LogX.w("uri is null.")
            return
        }
        try {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            asyncThread {
                val result = CodeUtils.parseCode(bitmap)
                runOnUiThread {
                    Log.d(TAG, "result:$result")
                    showToast(result)
                }
            }
        } catch (e: Exception) {
            LogX.w(e)
        }
    }

    private fun asyncThread(runnable: Runnable) {
        executor.execute(runnable)
    }

    private fun startScan(cls: Class<*>) {
        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.`in`, R.anim.out)
        val intent = Intent(this, cls)
        scanLauncher.launch(intent, optionsCompat)
    }

    private fun startGenerateCodeActivity(isQRCode: Boolean, title: String) {
        val intent = Intent(this, CodeActivity::class.java)
        intent.putExtra(KEY_IS_QR_CODE, isQRCode)
        intent.putExtra(KEY_TITLE, title)
        startActivity(intent)
    }

    @SuppressLint("NonConstantResourceId")
    fun onClick(v: View) {
        when (v.id) {
            R.id.btnMultiFormat -> startScan(MultiFormatScanActivity::class.java)
            R.id.btnQRCode -> startScan(QRCodeScanActivity::class.java)
            R.id.btnFullQRCode -> startScan(FullScreenQRCodeScanActivity::class.java)
            R.id.btnPickPhoto -> photoPickerLauncher.launch("image/*")
            R.id.btnGenerateQrCode -> startGenerateCodeActivity(true, (v as Button).text.toString())
            R.id.btnGenerateBarcode -> startGenerateCodeActivity(false, (v as Button).text.toString())
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        const val KEY_TITLE = "key_title"
        const val KEY_IS_QR_CODE = "key_code"
        const val REQUEST_CODE_SCAN = 0x01
        const val REQUEST_CODE_PHOTO = 0x02
    }
}
