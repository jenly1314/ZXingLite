package com.king.zxing.analyze;

import android.graphics.ImageFormat;

import com.google.zxing.Result;
import com.king.camera.scan.AnalyzeResult;
import com.king.camera.scan.FrameMetadata;
import com.king.camera.scan.analyze.Analyzer;

import java.nio.ByteBuffer;
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
    public void analyze(@NonNull ImageProxy imageProxy, @NonNull OnAnalyzeListener<AnalyzeResult<Result>> listener) {

        if (!joinQueue.get()) {
            int imageSize = imageProxy.getWidth() * imageProxy.getHeight();
            byte[] bytes = new byte[imageSize + 2 * (imageSize / 4)];
            queue.add(bytes);
            joinQueue.set(true);
        }
        if (queue.isEmpty()) {
            return;
        }
        final byte[] nv21Data = queue.poll();

        try {
            int rotation = imageProxy.getImageInfo().getRotationDegrees();
            int width = imageProxy.getWidth();
            int height = imageProxy.getHeight();

            yuv_420_888toNv21(imageProxy, nv21Data);

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
            listener.onSuccess(null);
        }

    }

    /**
     * YUV420_888转NV21
     *
     * @param image
     * @param nv21
     */
    private void yuv_420_888toNv21(@NonNull ImageProxy image, byte[] nv21) {
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();
        yBuffer.rewind();
        uBuffer.rewind();
        vBuffer.rewind();

        int ySize = yBuffer.remaining();

        int position = 0;

        // Add the full y buffer to the array. If rowStride > 1, some padding may be skipped.
        for (int row = 0; row < image.getHeight(); row++) {
            yBuffer.get(nv21, position, image.getWidth());
            position += image.getWidth();
            yBuffer.position(Math.min(ySize, yBuffer.position() - image.getWidth() + yPlane.getRowStride()));
        }

        int chromaHeight = image.getHeight() / 2;
        int chromaWidth = image.getWidth() / 2;
        int vRowStride = vPlane.getRowStride();
        int uRowStride = uPlane.getRowStride();
        int vPixelStride = vPlane.getPixelStride();
        int uPixelStride = uPlane.getPixelStride();

        // Interleave the u and v frames, filling up the rest of the buffer. Use two line buffers to
        // perform faster bulk gets from the byte buffers.
        byte[] vLineBuffer = new byte[vRowStride];
        byte[] uLineBuffer = new byte[uRowStride];
        for (int row = 0; row < chromaHeight; row++) {
            vBuffer.get(vLineBuffer, 0, Math.min(vRowStride, vBuffer.remaining()));
            uBuffer.get(uLineBuffer, 0, Math.min(uRowStride, uBuffer.remaining()));
            int vLineBufferPosition = 0;
            int uLineBufferPosition = 0;
            for (int col = 0; col < chromaWidth; col++) {
                nv21[position++] = vLineBuffer[vLineBufferPosition];
                nv21[position++] = uLineBuffer[uLineBufferPosition];
                vLineBufferPosition += vPixelStride;
                uLineBufferPosition += uPixelStride;
            }
        }
    }

}
