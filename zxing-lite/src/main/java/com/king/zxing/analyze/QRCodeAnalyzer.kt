package com.king.zxing.analyze

import com.google.zxing.DecodeHintType
import com.google.zxing.Reader
import com.google.zxing.qrcode.QRCodeReader
import com.king.zxing.DecodeConfig
import com.king.zxing.DecodeFormatManager

/**
 * 二维码分析器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
@Suppress("unused")
open class QRCodeAnalyzer @JvmOverloads constructor(
    config: DecodeConfig? = DecodeConfig().setHints(DecodeFormatManager.QR_CODE_HINTS)
) : BarcodeFormatAnalyzer(config) {

    constructor(hints: Map<DecodeHintType, Any>?) : this(DecodeConfig().setHints(hints))

    override fun createReader(): Reader {
        return QRCodeReader()
    }
}
