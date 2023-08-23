package com.king.zxing.analyze;

import com.google.zxing.DecodeHintType;
import com.google.zxing.Reader;
import com.google.zxing.qrcode.QRCodeReader;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DecodeFormatManager;

import java.util.Map;

import androidx.annotation.Nullable;

/**
 * 二维码分析器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class QRCodeAnalyzer extends BarcodeFormatAnalyzer {

    public QRCodeAnalyzer() {
        this(new DecodeConfig().setHints(DecodeFormatManager.QR_CODE_HINTS));
    }

    public QRCodeAnalyzer(@Nullable Map<DecodeHintType, Object> hints) {
        this(new DecodeConfig().setHints(hints));
    }

    public QRCodeAnalyzer(@Nullable DecodeConfig config) {
        super(config);
    }

    @Override
    public Reader createReader() {
        return new QRCodeReader();
    }

}
