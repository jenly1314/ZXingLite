package com.king.zxing.analyze;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;

import com.google.zxing.Result;
import com.king.zxing.util.LogUtils;

import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

/**
 * 图像分析器
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class ImageAnalyzer implements Analyzer {

    /**
     * 分析图像数据
     * @param data
     * @param width
     * @param height
     */
    @Nullable
    public abstract Result analyze(byte[] data, int width, int height);

    @Override
    public Result analyze(@NonNull ImageProxy image) {
        if(image.getFormat() == ImageFormat.YUV_420_888){
            @SuppressLint("UnsafeExperimentalUsageError")
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            return analyze(data,image.getWidth(),image.getHeight());
        }
        LogUtils.w("imageFormat: " + image.getFormat());
        return null;
    }

}
