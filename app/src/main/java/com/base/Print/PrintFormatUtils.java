package com.base.Print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * 打印格式
 * Created by john on 17-3-23.
 */

public class PrintFormatUtils {
    // 对齐方式
    public static final int ALIGN_LEFT = 0;     // 靠左
    public static final int ALIGN_CENTER = 1;   // 居中
    public static final int ALIGN_RIGHT = 2;    // 靠右

    //字体大小
    public static final int FONT_NORMAL = 0;    // 正常
    public static final int FONT_MIDDLE = 1;    // 中等
    public static final int FONT_BIG = 2;       // 大

    //加粗模式
    public static final int FONT_BOLD = 0;              // 字体加粗
    public static final int FONT_BOLD_CANCEL = 1;       // 取消加粗

    /**
     * 打印二维码
     * @param qrCode
     * @return
     */
    public static String getQrCodeCmd(String qrCode) {
        byte[] data;
        int store_len = qrCode.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);

        // QR Code: Select the model
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x08};

        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};

        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};

        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};

        data = byteMerger(modelQR, sizeQR);
        data = byteMerger(data, errorQR);
        data = byteMerger(data, storeQR);
        data = byteMerger(data, qrCode.getBytes());
        data = byteMerger(data, printQR);

        return new String(data);
    }

    /**
     * 打印条码
     * @param barcode
     * @return
     */
    public static String getBarcodeCmd(String barcode) {
        // 打印 Code-128 条码时需要使用字符集前缀
        // "{A" 表示大写字母
        // "{B" 表示所有字母，数字，符号
        // "{C" 表示数字，可以表示 00 - 99 的范围


        byte[] data;

        String btEncode;

        if (barcode.length() < 18) {
            // 字符长度小于15的时候直接输出字符串
            btEncode = "{B" + barcode;
        } else {
            // 否则做一点优化

            int startPos = 0;
            btEncode = "{B";

            for (int i = 0; i < barcode.length(); i++) {
                char curChar = barcode.charAt(i);

                if (curChar < 48 || curChar > 57 || i == (barcode.length() - 1)) {
                    // 如果是非数字或者是最后一个字符

                    if (i - startPos >= 10) {
                        if (startPos == 0) {
                            btEncode = "";
                        }

                        btEncode += "{C";

                        boolean isFirst = true;
                        int numCode = 0;

                        for (int j = startPos; j < i; j++) {

                            if (isFirst) { // 处理第一位
                                numCode = (barcode.charAt(j) - 48) * 10;
                                isFirst = false;
                            } else { // 处理第二位
                                numCode += (barcode.charAt(j) - 48);
                                btEncode += (char) numCode;
                                isFirst = true;
                            }

                        }

                        btEncode += "{B";

                        if (!isFirst) {
                            startPos = i - 1;
                        } else {
                            startPos = i;
                        }
                    }

                    for (int k = startPos; k <= i; k++) {
                        btEncode += barcode.charAt(k);
                    }
                    startPos = i + 1;
                }

            }
        }


        // 设置 HRI 的位置，02 表示下方
        byte[] hriPosition = {(byte) 0x1d, (byte) 0x48, (byte) 0x02};
        // 最后一个参数表示宽度 取值范围 1-6 如果条码超长则无法打印
        byte[] width = {(byte) 0x1d, (byte) 0x77, (byte) 0x02};
        byte[] height = {(byte) 0x1d, (byte) 0x68, (byte) 0xfe};
        // 最后两个参数 73 ： CODE 128 || 编码的长度
        byte[] barcodeType = {(byte) 0x1d, (byte) 0x6b, (byte) 73, (byte) btEncode.length()};
        byte[] print = {(byte) 10, (byte) 0};

        data = PrintFormatUtils.byteMerger(hriPosition, width);
        data = PrintFormatUtils.byteMerger(data, height);
        data = PrintFormatUtils.byteMerger(data, barcodeType);
        data = PrintFormatUtils.byteMerger(data, btEncode.getBytes());
        data = PrintFormatUtils.byteMerger(data, print);

        return new String(data);
    }

    /**
     * 切纸
     * @return
     */
    public static String getCutPaperCmd() {
        // 走纸并切纸，最后一个参数控制走纸的长度
        byte[] data = {(byte) 0x1d, (byte) 0x56, (byte) 0x42, (byte) 0x15};

        return new String(data);
    }

    /**
     * 对齐方式
     * @param alignMode
     * @return
     */
    public static String getAlignCmd(int alignMode) {
        byte[] data = {(byte) 0x1b, (byte) 0x61, (byte) 0x0};
        if (alignMode == ALIGN_LEFT) {
            data[2] = (byte) 0x00;
        } else if (alignMode == ALIGN_CENTER) {
            data[2] = (byte) 0x01;
        } else if (alignMode == ALIGN_RIGHT) {
            data[2] = (byte) 0x02;
        }

        return new String(data);
    }

    /**
     * 字体大小
     * @param fontSize
     * @return
     */
    public static String getFontSizeCmd(int fontSize) {
        byte[] data = {(byte) 0x1d, (byte) 0x21, (byte) 0x0};
        if (fontSize == FONT_NORMAL) {
            data[2] = (byte) 0x00;
        } else if (fontSize == FONT_MIDDLE) {
            data[2] = (byte) 0x01;
        } else if (fontSize == FONT_BIG) {
            data[2] = (byte) 0x11;
        }

        return new String(data);
    }

    /**
     * 加粗模式
     * @param fontBold
     * @return
     */
    public static String getFontBoldCmd(int fontBold) {
        byte[] data = {(byte) 0x1b, (byte) 0x45, (byte) 0x0};

        if (fontBold == FONT_BOLD) {
            data[2] = (byte) 0x01;
        } else if (fontBold == FONT_BOLD_CANCEL) {
            data[2] = (byte) 0x00;
        }

        return new String(data);
    }

    /**
     * 打开钱箱
     * @return
     */
    public static String getOpenDrawerCmd() {
        byte[] data = new byte[4];
        data[0] = 0x10;
        data[1] = 0x14;
        data[2] = 0x00;
        data[3] = 0x00;

        return new String(data);
    }

    /**
     * 字符串转字节数组
     * @param str
     * @return
     */
    public static byte[] stringToBytes(String str) {
        byte[] data = null;

        try {
            byte[] strBytes = str.getBytes("utf-8");

            data = (new String(strBytes, "utf-8")).getBytes("gbk");
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        }

        return data;
    }

    /**
     * 字节数组合并
     * @param bytesA
     * @param bytesB
     * @return
     */
    public static byte[] byteMerger(byte[] bytesA, byte[] bytesB) {
        byte[] bytes = new byte[bytesA.length + bytesB.length];
        System.arraycopy(bytesA, 0, bytes, 0, bytesA.length);
        System.arraycopy(bytesB, 0, bytes, bytesA.length, bytesB.length);
        return bytes;
    }
    /*************************************************************************
     * 我们的热敏打印机是RP-POS80S或RP-POS80P或RP-POS80CS或RP-POS80CP打印机
     * 360*360的图片，8个字节（8个像素点）是一个二进制，将二进制转化为十进制数值
     * y轴：24个像素点为一组，即360就是15组（0-14）
     * x轴：360个像素点（0-359）
     * 里面的每一组（24*360），每8个像素点为一个二进制，（每组有3个，3*8=24）
     **************************************************************************/
    /**
     * 把一张Bitmap图片转化为打印机可以打印的bit(将图片压缩为360*360)
     * 效率很高（相对于下面）
     * @param bit
     * @return
     */
    public static byte[] draw2PxPoint(Bitmap bit) {
        byte[] data = new byte[16290];
        int k = 0;
        for (int j = 0; j < 15; j++) {
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33; // m=33时，选择24点双密度打印，分辨率达到200DPI。
            data[k++] = 0x68;
            data[k++] = 0x01;
            for (int i = 0; i < 360; i++) {
                for (int m = 0; m < 3; m++) {
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bit);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;
        }
        return data;
    }

    /**
     * 把一张Bitmap图片转化为打印机可以打印的bit
     * @param bit
     * @return
     */
    public static byte[] pic2PxPoint(Bitmap bit){
        long start = System.currentTimeMillis();
        byte[] data = new byte[16290];
        int k = 0;
        for (int i = 0; i < 15; i++) {
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33; // m=33时，选择24点双密度打印，分辨率达到200DPI。
            data[k++] = 0x68;
            data[k++] = 0x01;
            for (int x = 0; x < 360; x++) {
                for (int m = 0; m < 3; m++) {
                    byte[]  by = new byte[8];
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(x, i * 24 + m * 8 +7-n, bit);
                        by[n] = b;
                    }
                    data[k] = (byte) changePointPx1(by);
                    k++;
                }
            }
            data[k++] = 10;
        }
        long end = System.currentTimeMillis();
        long str = end - start;
        Log.i("TAG", "str:" + str);
        return data;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     * @param x  横坐标
     * @param y     纵坐标
     * @param bit 位图
     * @return
     */
    public static byte px2Byte(int x, int y, Bitmap bit) {
        byte b;
        int pixel = bit.getPixel(x, y);
        int red = (pixel & 0x00ff0000) >> 16; // 取高两位
        int green = (pixel & 0x0000ff00) >> 8; // 取中两位
        int blue = pixel & 0x000000ff; // 取低两位
        int gray = RGB2Gray(red, green, blue);
        if ( gray < 128 ){
            b = 1;
        } else {
            b = 0;
        }
        return b;
    }

    /**
     * 图片灰度的转化
     * @param r
     * @param g
     * @param b
     * @return
     */
    private static int RGB2Gray(int r, int g, int b){
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return  gray;
    }

    /**
     * 对图片进行压缩（去除透明度）
     * @param bitmapOrg
     */
    public static Bitmap compressPic(Bitmap bitmapOrg) {
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = 360;
        int newHeight = 360;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmapOrg, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }


    /**
     * 对图片进行压缩(不去除透明度)
     * @param bitmapOrg
     */
    public static Bitmap compressBitmap(Bitmap bitmapOrg) {
        // 加载需要操作的图片，这里是一张图片
//        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.alipay);
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = 360;
        int newHeight = 360;
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,height, matrix, true);
        // 将上面创建的Bitmap转换成Drawable对象，使得其可以使用在ImageView, ImageButton中
//        BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
        return resizedBitmap;
    }

    /**
     * 将[1,0,0,1,0,0,0,1]这样的二进制转为化十进制的数值（效率更高）
     * @param arry
     * @return
     */
    public static int changePointPx1(byte[] arry){
        int v = 0;
        for (int j = 0; j <arry.length; j++) {
            if( arry[j] == 1) {
                v = v | 1 << j;
            }
        }
        return v;
    }

    /**
     * 将[1,0,0,1,0,0,0,1]这样的二进制转为化十进制的数值
     * @param arry
     * @return
     */
    public byte changePointPx(byte[] arry){
        byte v = 0;
        for (int i = 0; i < 8; i++) {
            v += v + arry[i];
        }
        return v;
    }

    /**
     * 得到位图的某个点的像素值
     * @param bitmap
     * @return
     */
    public byte[] getPicPx(Bitmap bitmap){
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];// 保存所有的像素的数组，图片宽×高
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int clr = pixels[i];
            int red = (clr & 0x00ff0000) >> 16; // 取高两位
            int green = (clr & 0x0000ff00) >> 8; // 取中两位
            int blue = clr & 0x000000ff; // 取低两位
            System.out.println("r=" + red + ",g=" + green + ",b=" + blue);
        }
        return null;
    }
}