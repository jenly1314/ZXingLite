package com.king.zxing.analyze;

import android.graphics.ImageFormat;

import com.google.zxing.Result;
import com.king.camera.scan.AnalyzeResult;
import com.king.camera.scan.FrameMetadata;
import com.king.camera.scan.analyze.Analyzer;
import com.king.camera.scan.util.ImageUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

/**
 * 图像分析器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
public abstract class ImageAnalyzer implements Analyzer<Result> {

    private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean joinQueue = new AtomicBoolean(false);

    /**
     * 分析图像数据
     *
     * @param data
     * @param width
     * @param height
     */
    @Nullable
    public abstract Result analyze(byte[] data, int width, int height);

    @Override
    public void analyze(@NonNull ImageProxy imageProxy, @NonNull OnAnalyzeListener<Result> listener) {

        if (!joinQueue.get()) {
            int imageSize = imageProxy.getWidth() * imageProxy.getHeight();
            byte[] bytes = new byte[imageSize + 2 * (imageSize / 4)];
            queue.add(bytes);
            joinQueue.set(true);
        }

        final byte[] nv21Data = queue.poll();
        if(nv21Data == null) {
            return;
        }

        try {
            int rotation = imageProxy.getImageInfo().getRotationDegrees();
            int width = imageProxy.getWidth();
            int height = imageProxy.getHeight();

            ImageUtils.yuv_420_888toNv21(imageProxy, nv21Data);

            Result result;
            if (rotation == 90 || rotation == 270) {
                byte[] rotatedData = new byte[nv21Data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        rotatedData[x * height + height - y - 1] = nv21Data[x + y * width];
                    }
                }
                result = analyze(rotatedData, height, width);
            } else {
                result = analyze(nv21Data, width, height);
            }
            if (result != null) {
                FrameMetadata frameMetadata = new FrameMetadata(
                        width,
                        height,
                        rotation);
                joinQueue.set(false);
                listener.onSuccess(new AnalyzeResult<>(nv21Data, ImageFormat.NV21, frameMetadata, result));
            } else {
                queue.add(nv21Data);
                listener.onFailure(null);
            }

        } catch (Exception e) {
            queue.add(nv21Data);
            listener.onFailure(null);
        }

    }
}
