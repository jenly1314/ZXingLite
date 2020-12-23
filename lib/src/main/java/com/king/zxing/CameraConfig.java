package com.king.zxing;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CameraConfig {

    public CameraConfig(){

    }

    @NonNull
    public Preview options(@NonNull Preview.Builder builder){
        return builder.build();
    }

    @NonNull
    public CameraSelector options(@NonNull CameraSelector.Builder builder){
        return builder.build();
    }

    @NonNull
    public ImageAnalysis options(@NonNull ImageAnalysis.Builder builder){
        return builder.build();
    }

}
