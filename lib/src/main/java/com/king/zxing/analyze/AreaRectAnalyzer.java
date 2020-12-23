package com.king.zxing.analyze;

import android.graphics.Rect;

import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.DecodeConfig;

import java.util.Map;

import androidx.annotation.Nullable;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class AreaRectAnalyzer extends ImageAnalyzer {

    DecodeConfig mDecodeConfig;
    Map<DecodeHintType,?> mHints;
    boolean isMultiDecode = true;

    public AreaRectAnalyzer(@Nullable DecodeConfig config){
        this.mDecodeConfig = config;
        if(config != null){
            mHints = config.getHints();
            isMultiDecode = config.isMultiDecode();
        }else{
            mHints = DecodeFormatManager.DEFAULT_HINTS;
        }

    }

    @Nullable
    @Override
    public Result analyze(byte[] data, int width, int height) {
        if(mDecodeConfig == null || mDecodeConfig.isFullAreaScan()){
            //mDecodeConfig为空或者支持全区域扫码识别时，直接使用全区域进行扫码识别
            return analyze(data,width,height,0,0,width,height);
        }

        Rect rect = mDecodeConfig.getAnalyzeAreaRect();
        if(rect != null){//如果分析区域不为空，则使用指定的区域进行扫码识别
            return analyze(data,width,height,rect.left,rect.top,rect.width(),rect.height());
        }
        //如果分析区域为空，则通过识别区域比例和相关的偏移量计算出最终的区域进行扫码识别
        int size = (int)(Math.min(width,height) * mDecodeConfig.getAreaRectRatio());
        int left = (width-size)/2 + mDecodeConfig.getAreaRectHorizontalOffset();
        int top = (height-size)/2 + mDecodeConfig.getAreaRectVerticalOffset();

        return analyze(data,width,height,left,top,size,size);

    }

    abstract Result analyze(byte[] data, int dataWidth, int dataHeight,int left,int top,int width,int height);

}
