package com.king.zxing.analyze;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.king.zxing.DecodeConfig;
import com.king.zxing.util.LogUtils;


import java.util.Map;

import androidx.annotation.Nullable;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class MultiFormatAnalyzer extends AreaRectAnalyzer {

    MultiFormatReader mReader;

    public MultiFormatAnalyzer(){
        this((DecodeConfig)null);
    }

    public MultiFormatAnalyzer(@Nullable Map<DecodeHintType,Object> hints){
        this(new DecodeConfig().setHints(hints));
    }

    public MultiFormatAnalyzer(@Nullable DecodeConfig config) {
        super(config);
        initReader();
    }

    private void initReader(){
        mReader = new MultiFormatReader();
    }

    @Nullable
    @Override
    public Result analyze(byte[] data, int dataWidth, int dataHeight,int left,int top,int width,int height) {
        Result rawResult = null;
        try {
            long start = System.currentTimeMillis();
            mReader.setHints(mHints);
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,dataWidth,dataHeight,left,top,width,height,false);
            rawResult = decodeInternal(source,isMultiDecode);

            if(rawResult == null && mDecodeConfig != null){
                if(rawResult == null && mDecodeConfig.isSupportVerticalCode()){
                    byte[] rotatedData = new byte[data.length];
                    for (int y = 0; y < dataHeight; y++) {
                        for (int x = 0; x < dataWidth; x++){
                            rotatedData[x * dataHeight + dataHeight - y - 1] = data[x + y * dataWidth];
                        }
                    }
                    rawResult = decodeInternal(new PlanarYUVLuminanceSource(rotatedData,dataHeight,dataWidth,top,left,height,width,false),mDecodeConfig.isSupportVerticalCodeMultiDecode());
                }

                if(rawResult == null && mDecodeConfig.isSupportLuminanceInvert()){
                    rawResult = decodeInternal(source.invert(),mDecodeConfig.isSupportLuminanceInvertMultiDecode());
                }
            }
            if(rawResult != null){
                long end = System.currentTimeMillis();
                LogUtils.d("Found barcode in " + (end - start) + " ms");
            }
        } catch (Exception e) {

        }finally {
            mReader.reset();
        }
        return rawResult;
    }


    private Result decodeInternal(LuminanceSource source,boolean isMultiDecode){
        Result result = null;
        try{
            try{
                //采用HybridBinarizer解析
                result = mReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
            }catch (Exception e){

            }
            if(isMultiDecode && result == null){
                //如果没有解析成功，再采用GlobalHistogramBinarizer解析一次
                result = mReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
            }
        }catch (Exception e){

        }
        return result;
    }
}
