package com.king.zxing.analyze;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
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
    public Result analyze(@NonNull ImageProxy image,int orientation) {
        if(image.getFormat() == ImageFormat.YUV_420_888){
            @SuppressLint("UnsafeExperimentalUsageError")
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            int width = image.getWidth();
            int height = image.getHeight();
            if(orientation == Configuration.ORIENTATION_PORTRAIT){
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++){
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                    }
                }
                return analyze(rotatedData,height,width);
            }
            return analyze(data,width,height);
        }else{
            LogUtils.w("imageFormat: " + image.getFormat());
        }
        return null;
    }

}
