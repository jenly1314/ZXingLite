package com.king.zxing.analyze;

import com.google.zxing.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

/**
 * 分析器
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface Analyzer {

    @Nullable
    Result analyze(@NonNull ImageProxy image);
}
