package com.base.zxing.decoding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/14.
 */

public class ZXingUtils {
    //二维码扫描界面到相册获取图片的请求码
    public static final int ScanPhotosRequestCode = 999;
    //二维码扫描历史界面返回码
    public static final int ScanHistoryResultCode = 777;
    //二维码扫描返回的Intent结果
    public static final String ScanResult = "ScanResult";
    public static final String ScanResultType = "ScanResultType";
    //是否显示历史信息
    public static final String ScanIsShowHistory = "ScanIsShowHistory";
    //二维码扫描返回码
    public static final int resultCode=300;


    public static final Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);

    static {
        List<BarcodeFormat> allFormats = new ArrayList<>();
        allFormats.add(BarcodeFormat.AZTEC);
        allFormats.add(BarcodeFormat.CODABAR);
        allFormats.add(BarcodeFormat.CODE_39);
        allFormats.add(BarcodeFormat.CODE_93);
        allFormats.add(BarcodeFormat.CODE_128);
        allFormats.add(BarcodeFormat.DATA_MATRIX);
        allFormats.add(BarcodeFormat.EAN_8);
        allFormats.add(BarcodeFormat.EAN_13);
        allFormats.add(BarcodeFormat.ITF);
        allFormats.add(BarcodeFormat.MAXICODE);
        allFormats.add(BarcodeFormat.PDF_417);
        allFormats.add(BarcodeFormat.QR_CODE);
        allFormats.add(BarcodeFormat.RSS_14);
        allFormats.add(BarcodeFormat.RSS_EXPANDED);
        allFormats.add(BarcodeFormat.UPC_A);
        allFormats.add(BarcodeFormat.UPC_E);
        allFormats.add(BarcodeFormat.UPC_EAN_EXTENSION);

        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, allFormats);
        // 解码设置编码方式为：utf-8，
        HINTS.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //优化精度
        HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        //复杂模式，开启PURE_BARCODE模式
        //HINTS.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
    }


    public static final boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            // 是否是Unicode编码,除了"�"这个字符.这个字符要另外处理
            if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')
                    || ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    /**
     * Resize the bitmap
     *
     * @param bitmap 图片引用
     * @param width 宽度
     * @param height 高度
     * @return 缩放之后的图片引用
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }
    //------解析图片-----代码来自：https://github.com/bingoogolapple/BGAQRCode-Android----感谢
    /**
     * 同步解析bitmap二维码。该方法是耗时操作，请在子线程中调用。
     *
     * @param bitmap 要解析的二维码图片
     * @return 返回二维码图片里的内容 或 null
     */
    public static Result syncDecodeQRCode(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth()/2;
            int height = bitmap.getHeight()/2;
            Bitmap smallBitmap= zoomBitmap(bitmap, width, height);
            bitmap.recycle();

            BitmapLuminanceSource source = new BitmapLuminanceSource(smallBitmap);
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            multiFormatReader.setHints(HINTS);
            Result result =multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
            if(result != null){
                return result;
            }else{
                return new Result("",null,null, BarcodeFormat.AZTEC);
            }
        } catch (Exception e) {
            return new Result("",null,null, BarcodeFormat.AZTEC);
        }
    }
    /**
     * 中文乱码
     *
     * @return
     */
    public static String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

    //---------
    public static int dip2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
