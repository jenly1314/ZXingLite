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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.king.camera.scan.CameraScan;
import com.king.zxing.util.CodeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 扫码示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";

    public static final int REQUEST_CODE_SCAN = 0x01;
    public static final int REQUEST_CODE_PHOTO = 0x02;

    private Toast toast;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    String result = CameraScan.parseScanResult(data);
                    showToast(result);
                    break;
                case REQUEST_CODE_PHOTO:
                    parsePhoto(data);
                    break;
            }

        }
    }

    private void showToast(String text) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this, String.valueOf(text), Toast.LENGTH_SHORT);
        toast.show();
    }

    private void parsePhoto(Intent data) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            //异步解析
            asyncThread(() -> {
                final String result = CodeUtils.parseCode(bitmap);
                // 如果只需识别二维码，建议使用：parseQRCode；（因为识别的格式越明确，误识别率越低。）
//                final String result = CodeUtils.parseQRCode(bitmap);
                runOnUiThread(() -> {
                    Log.d(TAG, "result:" + result);
                    showToast(result);
                });

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void asyncThread(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * 扫码
     *
     * @param cls
     */
    private void startScan(Class<?> cls) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.in, R.anim.out);
        Intent intent = new Intent(this, cls);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
    }

    /**
     * 生成二维码/条形码
     *
     * @param isQRCode
     */
    private void startGenerateCodeActivity(boolean isQRCode, String title) {
        Intent intent = new Intent(this, CodeActivity.class);
        intent.putExtra(KEY_IS_QR_CODE, isQRCode);
        intent.putExtra(KEY_TITLE, title);
        startActivity(intent);
    }

    /**
     * 开始选择图片
     */
    private void startPickPhoto() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMultiFormat:
                startScan(MultiFormatScanActivity.class);
                break;
            case R.id.btnQRCode:
                startScan(QRCodeScanActivity.class);
                break;
            case R.id.btnFullQRCode:
                startScan(FullScreenQRCodeScanActivity.class);
                break;
            case R.id.btnPickPhoto:
                startPickPhoto();
                break;
            case R.id.btnGenerateQrCode:
                startGenerateCodeActivity(true, ((Button) v).getText().toString());
                break;
            case R.id.btnGenerateBarcode:
                startGenerateCodeActivity(false, ((Button) v).getText().toString());
                break;
        }

    }
}
