package com.king.zxing.analyze;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
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
public abstract class BarcodeFormatAnalyzer extends AreaRectAnalyzer {

    private Reader mReader;

    public BarcodeFormatAnalyzer(@Nullable Map<DecodeHintType,Object> hints){
        this(new DecodeConfig().setHints(hints));
    }

    public BarcodeFormatAnalyzer(@Nullable DecodeConfig config) {
        super(config);
        initReader();
    }

    private void initReader(){
        mReader = createReader();
    }

    @Nullable
    @Override
    public Result analyze(byte[] data, int dataWidth, int dataHeight,int left,int top,int width,int height) {
        Result rawResult = null;
        if(mReader != null){
            try {
                long start = System.currentTimeMillis();
                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,dataWidth,dataHeight,left,top,width,height,false);
                rawResult = decodeInternal(source,isMultiDecode);

                if(rawResult == null && mDecodeConfig != null){
                    if(rawResult == null && mDecodeConfig.isSupportVerticalCode()){
                        byte[] rotatedData = new byte[data.length];
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++)
                                rotatedData[x * height + height - y - 1] = data[x + y * width];
                        }
                        rawResult = decodeInternal(new PlanarYUVLuminanceSource(rotatedData,dataHeight,dataWidth,top,left,height,width,false),mDecodeConfig.isSupportVerticalCodeMultiDecode());
                    }

                    if(mDecodeConfig.isSupportLuminanceInvert()){
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
        }
        return rawResult;
    }

    private Result decodeInternal(LuminanceSource source,boolean isMultiDecode){
        Result result = null;
        try{
            try{
                //采用HybridBinarizer解析
                result = mReader.decode(new BinaryBitmap(new HybridBinarizer(source)),mHints);
            }catch (Exception e){

            }
            if(isMultiDecode && result == null){
                //如果没有解析成功，再采用GlobalHistogramBinarizer解析一次
                result = mReader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)),mHints);
            }
        }catch (Exception e){

        }
        return result;
    }

    public abstract Reader createReader();

}
