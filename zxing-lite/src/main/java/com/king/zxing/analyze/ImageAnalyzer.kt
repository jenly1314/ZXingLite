package com.king.zxing.analyze

import android.graphics.ImageFormat
import androidx.camera.core.ImageProxy
import com.google.zxing.Result
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.FrameMetadata
import com.king.camera.scan.analyze.Analyzer
import com.king.camera.scan.util.ImageUtils
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 图像分析器
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
abstract class ImageAnalyzer : Analyzer<Result> {

    private val queue: Queue<ByteArray> = ConcurrentLinkedQueue()
    private val joinQueue = AtomicBoolean(false)

    abstract fun analyze(data: ByteArray, width: Int, height: Int): Result?

    override fun analyze(imageProxy: ImageProxy, listener: Analyzer.OnAnalyzeListener<Result>) {
        if (!joinQueue.get()) {
            val imageSize = imageProxy.width * imageProxy.height
            val bytes = ByteArray(imageSize + 2 * (imageSize / 4))
            queue.add(bytes)
            joinQueue.set(true)
        }

        val nv21Data = queue.poll() ?: return

        try {
            val rotation = imageProxy.imageInfo.rotationDegrees
            val width = imageProxy.width
            val height = imageProxy.height

            ImageUtils.yuv_420_888toNv21(imageProxy, nv21Data)

            val result = if (rotation == 90 || rotation == 270) {
                val rotatedData = ByteArray(nv21Data.size)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        rotatedData[x * height + height - y - 1] = nv21Data[x + y * width]
                    }
                }
                analyze(rotatedData, height, width)
            } else {
                analyze(nv21Data, width, height)
            }
            if (result != null) {
                val frameMetadata = FrameMetadata(width, height, rotation)
                joinQueue.set(false)
                listener.onSuccess(AnalyzeResult(nv21Data, ImageFormat.NV21, frameMetadata, result))
            } else {
                queue.add(nv21Data)
                listener.onFailure(null)
            }
        } catch (_: Exception) {
            queue.add(nv21Data)
            listener.onFailure(null)
        }
    }
}
