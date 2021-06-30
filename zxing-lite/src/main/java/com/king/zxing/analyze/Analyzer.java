package com.king.zxing.analyze;

import com.google.zxing.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import android.content.res.Configuration;

/**
 * 分析器
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface Analyzer {

    /**
     * Analyzes an image to produce a result.
     * @param image The image to analyze
     * @param orientation {@link Configuration#ORIENTATION_LANDSCAPE}, {@link Configuration#ORIENTATION_PORTRAIT}.
     * @return
     */
    @Nullable
    Result analyze(@NonNull ImageProxy image,int orientation);
}
