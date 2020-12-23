package com.king.zxing.app;

import android.os.Bundle;
import android.widget.TextView;

import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.analyze.MultiFormatAnalyzer;
import com.king.zxing.app.util.StatusBarUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class QRCodeActivity extends CaptureActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        StatusBarUtils.immersiveStatusBar(this,toolbar,0.2f);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra(MainActivity.KEY_TITLE));
    }


    @Override
    public int getLayoutId() {
        return R.layout.qr_code_activity;
    }

    @Override
    public void initCameraScan() {
        super.initCameraScan();
        //在启动预览之前，设置分析器，只识别二维码，如果只有识别二维码的需求，这样效率更多高
        getCameraScan()
                .setVibrate(true)
                .setAnalyzer(new MultiFormatAnalyzer(DecodeFormatManager.QR_CODE_HINTS));
    }

    @Override
    public boolean onScanResultCallback(Result result) {
        return super.onScanResultCallback(result);
    }
}
