package com.king.zxing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 解码格式管理器
 * <p>
 * 将常见的一些解码配置已根据条形码类型进行了几大划分，可根据需要找到符合的划分配置类型直接使用。
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class DecodeFormatManager {

    /**
     * 所有的
     */
    public static final Map<DecodeHintType, Object> ALL_HINTS = new EnumMap<>(DecodeHintType.class);
    /**
     * CODE_128 (最常用的一维码)
     */
    public static final Map<DecodeHintType, Object> CODE_128_HINTS = createDecodeHint(BarcodeFormat.CODE_128);
    /**
     * QR_CODE (最常用的二维码)
     */
    public static final Map<DecodeHintType, Object> QR_CODE_HINTS = createDecodeHint(BarcodeFormat.QR_CODE);
    /**
     * 一维码
     */
    public static final Map<DecodeHintType, Object> ONE_DIMENSIONAL_HINTS = new EnumMap<>(DecodeHintType.class);
    /**
     * 二维码
     */
    public static final Map<DecodeHintType, Object> TWO_DIMENSIONAL_HINTS = new EnumMap<>(DecodeHintType.class);
    /**
     * 默认
     */
    public static final Map<DecodeHintType, Object> DEFAULT_HINTS = new EnumMap<>(DecodeHintType.class);

    static {
        //all hints
        addDecodeHintTypes(ALL_HINTS, getAllFormats());
        //one dimension
        addDecodeHintTypes(ONE_DIMENSIONAL_HINTS, getOneDimensionalFormats());
        //Two dimension
        addDecodeHintTypes(TWO_DIMENSIONAL_HINTS, getTwoDimensionalFormats());
        //default hints
        addDecodeHintTypes(DEFAULT_HINTS, getDefaultFormats());
    }

    /**
     * 所有支持的{@link BarcodeFormat}
     *
     * @return
     */
    private static List<BarcodeFormat> getAllFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.AZTEC);
        list.add(BarcodeFormat.CODABAR);
        list.add(BarcodeFormat.CODE_39);
        list.add(BarcodeFormat.CODE_93);
        list.add(BarcodeFormat.CODE_128);
        list.add(BarcodeFormat.DATA_MATRIX);
        list.add(BarcodeFormat.EAN_8);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.ITF);
        list.add(BarcodeFormat.MAXICODE);
        list.add(BarcodeFormat.PDF_417);
        list.add(BarcodeFormat.QR_CODE);
        list.add(BarcodeFormat.RSS_14);
        list.add(BarcodeFormat.RSS_EXPANDED);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.UPC_E);
        list.add(BarcodeFormat.UPC_EAN_EXTENSION);
        return list;
    }

    /**
     * 一维码
     * 包括如下几种格式：
     * {@link BarcodeFormat#CODABAR}
     * {@link BarcodeFormat#CODE_39}
     * {@link BarcodeFormat#CODE_93}
     * {@link BarcodeFormat#CODE_128}
     * {@link BarcodeFormat#EAN_8}
     * {@link BarcodeFormat#EAN_13}
     * {@link BarcodeFormat#ITF}
     * {@link BarcodeFormat#RSS_14}
     * {@link BarcodeFormat#RSS_EXPANDED}
     * {@link BarcodeFormat#UPC_A}
     * {@link BarcodeFormat#UPC_E}
     * {@link BarcodeFormat#UPC_EAN_EXTENSION}
     *
     * @return
     */
    private static List<BarcodeFormat> getOneDimensionalFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.CODABAR);
        list.add(BarcodeFormat.CODE_39);
        list.add(BarcodeFormat.CODE_93);
        list.add(BarcodeFormat.CODE_128);
        list.add(BarcodeFormat.EAN_8);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.ITF);
        list.add(BarcodeFormat.RSS_14);
        list.add(BarcodeFormat.RSS_EXPANDED);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.UPC_E);
        list.add(BarcodeFormat.UPC_EAN_EXTENSION);
        return list;
    }

    /**
     * 二维码
     * 包括如下几种格式：
     * {@link BarcodeFormat#AZTEC}
     * {@link BarcodeFormat#DATA_MATRIX}
     * {@link BarcodeFormat#MAXICODE}
     * {@link BarcodeFormat#PDF_417}
     * {@link BarcodeFormat#QR_CODE}
     *
     * @return
     */
    private static List<BarcodeFormat> getTwoDimensionalFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.AZTEC);
        list.add(BarcodeFormat.DATA_MATRIX);
        list.add(BarcodeFormat.MAXICODE);
        list.add(BarcodeFormat.PDF_417);
        list.add(BarcodeFormat.QR_CODE);
        return list;
    }

    /**
     * 默认支持的格式
     * 包括如下几种格式：
     * {@link BarcodeFormat#QR_CODE}
     * {@link BarcodeFormat#UPC_A}
     * {@link BarcodeFormat#EAN_13}
     * {@link BarcodeFormat#CODE_128}
     *
     * @return
     */
    private static List<BarcodeFormat> getDefaultFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.CODE_128);
        return list;
    }

    /**
     * 支持解码的格式
     *
     * @param barcodeFormats {@link BarcodeFormat}
     * @return
     */
    public static Map<DecodeHintType, Object> createDecodeHints(@NonNull BarcodeFormat... barcodeFormats) {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        addDecodeHintTypes(hints, Arrays.asList(barcodeFormats));
        return hints;
    }

    /**
     * 支持解码的格式
     *
     * @param barcodeFormat {@link BarcodeFormat}
     * @return
     */
    public static Map<DecodeHintType, Object> createDecodeHint(@NonNull BarcodeFormat barcodeFormat) {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        addDecodeHintTypes(hints, Collections.singletonList(barcodeFormat));
        return hints;
    }

    /**
     * @param hints
     * @param formats
     */
    private static void addDecodeHintTypes(Map<DecodeHintType, Object> hints, List<BarcodeFormat> formats) {
        // Image is known to be of one of a few possible formats.
        hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        // Spend more time to try to find a barcode; optimize for accuracy, not speed.
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        // Specifies what character encoding to use when decoding, where applicable (type String)
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
    }

}
