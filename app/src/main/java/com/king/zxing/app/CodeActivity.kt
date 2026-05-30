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

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.king.zxing.util.CodeUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 生成条形码/二维码示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class CodeActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvBarcodeFormat: TextView
    private lateinit var ivCode: ImageView
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.code_activity)
        ivCode = findViewById(R.id.ivCode)
        tvTitle = findViewById(R.id.tvTitle)
        tvBarcodeFormat = findViewById(R.id.tvBarcodeFormat)
        tvTitle.text = intent.getStringExtra(MainActivity.KEY_TITLE)
        val isQRCode = intent.getBooleanExtra(MainActivity.KEY_IS_QR_CODE, false)

        if (isQRCode) {
            tvBarcodeFormat.text = "BarcodeFormat: QR_CODE"
            createQRCode(getString(R.string.app_name))
        } else {
            tvBarcodeFormat.text = "BarcodeFormat: CODE_128"
            createBarCode("1234567890")
        }
    }

    private fun createQRCode(content: String) {
        executor.execute {
            val logo = BitmapFactory.decodeResource(resources, R.drawable.logo)
            val bitmap = CodeUtils.createQRCode(content, 600, logo)
            runOnUiThread { ivCode.setImageBitmap(bitmap) }
        }
    }

    private fun createBarCode(content: String) {
        executor.execute {
            val bitmap = CodeUtils.createBarCode(content, BarcodeFormat.CODE_128, 800, 200, null, true)
            runOnUiThread { ivCode.setImageBitmap(bitmap) }
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.ivLeft -> finish()
        }
    }
}
