package com.king.zxing.config;

import android.content.Context;
import android.util.DisplayMetrics;


import com.king.zxing.util.LogUtils;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;

/**
 * 相机配置：根据纵横比配置相机，使预览和输出的图像尽可能的接近屏幕比例
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class AspectRatioCameraConfig extends CameraConfig {

    private int mAspectRatio;

    public AspectRatioCameraConfig(Context context) {
        super();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        mAspectRatio = aspectRatio(width, height);
        LogUtils.d("aspectRatio:" + mAspectRatio);

    }

    private int aspectRatio(float width, float height){
        float ratio = Math.max(width, height) / Math.min(width, height);
        if (Math.abs(ratio - 4.0f / 3.0f) < Math.abs(ratio - 16.0f / 9.0f)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @NonNull
    @Override
    public Preview options(@NonNull Preview.Builder builder) {
        builder.setTargetAspectRatio(mAspectRatio);
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
        builder.setTargetAspectRatio(mAspectRatio);
        return super.options(builder);
    }
}
