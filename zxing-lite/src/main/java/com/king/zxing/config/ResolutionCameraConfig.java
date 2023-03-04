package com.king.zxing.config;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Size;

import com.king.zxing.util.LogUtils;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;

/**
 * 相机配置：根据尺寸配置相机的目标图像大小，使输出分析的图像的分辨率尽可能的接近屏幕尺寸
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class ResolutionCameraConfig extends CameraConfig {

    /**
     * 1080P
     */
    public static final int IMAGE_QUALITY_1080P = 1080;
    /**
     * 720P
     */
    public static final int IMAGE_QUALITY_720P = 720;


    private Size mTargetSize;

    /**
     * 构造
     *
     * @param context 上下文
     */
    public ResolutionCameraConfig(Context context) {
        this(context, IMAGE_QUALITY_1080P);
    }

    /**
     * 构造
     *
     * @param context      上下文
     * @param imageQuality 图像质量；此参数只是期望的图像质量，最终以实际计算结果为准
     */
    public ResolutionCameraConfig(Context context, int imageQuality) {
        super();
        initTargetResolutionSize(context, imageQuality);
    }

    /**
     * 初始化 {@link #mTargetSize}
     *
     * @param context      上下文
     * @param imageQuality 图像质量；此参数只是期望的图像质量，最终以实际计算结果为准
     */
    private void initTargetResolutionSize(Context context, int imageQuality) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        LogUtils.d(String.format(Locale.getDefault(), "displayMetrics:%d x %d", width, height));
        // 因为为了保持流畅性和性能，尽可能的限制在imageQuality（默认：1080p），在此前提下尽可能的找到屏幕接近的分辨率
        if (width < height) {
            int size = Math.min(width, imageQuality);
            float ratio = width / (float) height;
            if (ratio > 0.7F) {
                // 一般应用于平板
                mTargetSize = new Size(size, (int) (size / 3.0F * 4.0F));
            } else {
                mTargetSize = new Size(size, (int) (size / 9.0F * 16.0F));
            }
        } else {
            int size = Math.min(height, imageQuality);
            float ratio = height / (float) width;
            if (ratio > 0.7F) {
                // 一般应用于平板
                mTargetSize = new Size((int) (size / 3.0F * 4.0F), size);
            } else {
                mTargetSize = new Size((int) (size / 9.0F * 16.0F), size);
            }
        }
        LogUtils.d("targetSize:" + mTargetSize);
    }

    @NonNull
    @Override
    public Preview options(@NonNull Preview.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public CameraSelector options(@NonNull CameraSelector.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public ImageAnalysis options(@NonNull ImageAnalysis.Builder builder) {
        builder.setTargetResolution(mTargetSize);
        return super.options(builder);
    }
}
