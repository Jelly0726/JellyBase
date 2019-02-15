package com.base.screenColor;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * 获取屏幕坐标的颜色值
 * 原理：利用Android系统提供的投影功能把屏幕投影到ImageReader中，通过ImageReader获取到Bitmap，调用Bitmap的getPixel(x, y)方法获取到指定坐标的颜色。
 */
public class GBData {
    private static final String TAG = "GBData";
    static ImageReader reader;
    private static Bitmap bitmap;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getColor(int x, int y) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return -1;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            if (bitmap == null) {
                Log.w(TAG, "getColor: image is null");
                return -1;
            }
            return bitmap.getPixel(x, y);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        }
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        return bitmap.getPixel(x, y);
    }
}
