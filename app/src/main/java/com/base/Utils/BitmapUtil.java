package com.base.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.base.appManager.MyApplication;
import com.base.log.DebugLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片工具类
 */
public class BitmapUtil {
    // 扫描的三种方式
    public static enum ScannerType {
        RECEIVER, MEDIA
    }
    // 首先保存图片
    public static void saveImageToGallery(Context context, Bitmap bitmap, ScannerType type) {
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Cache");
        if (!appDir.exists()) {
            // 目录不存在 则创建
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 保存bitmap至本地
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (type == ScannerType.RECEIVER) {
                ScannerByReceiver(context, file.getAbsolutePath());
            } else if (type == ScannerType.MEDIA) {
                ScannerByMedia(context, file.getAbsolutePath());
            }
            if (!bitmap.isRecycled()) {
                // bitmap.recycle(); 当存储大图片时，为避免出现OOM ，及时回收Bitmap
                System.gc(); // 通知系统回收
            }
            Toast.makeText(context, "图片保存为" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }
    // 首先保存图片
    public static void saveBitmap(Context context, Bitmap bitmap) {
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Cache");
        if (!appDir.exists()) {
            // 目录不存在 则创建
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 保存bitmap至本地
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!bitmap.isRecycled()) {
                // bitmap.recycle(); 当存储大图片时，为避免出现OOM ，及时回收Bitmap
                System.gc(); // 通知系统回收
            }
            ScannerByMedia(context, file.getAbsolutePath());
            Toast.makeText(context, "图片保存为" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 保存Bitmap到sd卡中
     */
    private boolean saveImage(Context context,Bitmap bmp) {
        try {
            //保存到系统相册
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "title", "description");
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(file);
//            intent.setData(uri);
//            //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
//            context.sendBroadcast(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            return false;
        }
    }
    /** Receiver扫描更新图库图片 **/

    private static void ScannerByReceiver(Context context, String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + path)));
        DebugLog.v("TAG", "receiver scanner completed");
    }

    /** MediaScanner 扫描更新图库图片 **/

    private static void ScannerByMedia(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[] {path}, null, null);
        DebugLog.v("TAG", "media scanner completed");
    }
    /**
     * VectorDrawable 转为 Bitmap
     * @param context
     * @param drawableId
     * @return
     */
    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    /**
     * bitmap转字节数组
     * @param bitmap
     * @param format
     * @return
     */
    public static byte[] bitmap2Bytes(final Bitmap bitmap, final Bitmap.CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }
    /**
     * 字节数组转成bitmap
     * @param bytes
     * @return
     */
    public static Bitmap bytes2Bitmap(final byte[] bytes) {
        return (bytes == null || bytes.length == 0)
                ? null
                : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * drawable转成bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转成Drawable
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(final Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(MyApplication.getMyApp().getResources(), bitmap);
    }

    /**
     * drawable转字节数组
     * @param drawable
     * @param format
     * @return
     */
    public static byte[] drawable2Bytes(final Drawable drawable, final Bitmap.CompressFormat format) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /**
     * 字节数组转 drawable
     * @param bytes
     * @return
     */
    public static Drawable bytes2Drawable(final byte[] bytes) {
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    /**
     * view转bitmap
     * @param view
     * @return
     */
    public static Bitmap view2Bitmap(Activity activity, final View view) {
        if (view == null) return null;
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        // 整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //无黑边全屏
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap1=view.getDrawingCache();
        if (bitmap1==null){
            bitmap1=BitmapUtil.loadBitmapFromView(view, false);
        }
        Bitmap bitmap = Bitmap.createBitmap(bitmap1); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }
    /**
     * view转bitmap
     * @param activity
     * @param layoutId
     * @return
     */
    public static Bitmap getViewBitmap(Activity activity,int layoutId) {
        View view = activity.getLayoutInflater().inflate(layoutId, null);
//        DisplayMetrics metric = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;     // 屏幕宽度（像素）
//        int height = metric.heightPixels;   // 屏幕高度（像素）
//        // 整个View的大小 参数是左上角 和右下角的坐标
//        view.layout(0, 0, width, height);
//        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
//        int measuredHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
//        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
//         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
//         */
//        view.measure(measuredWidth, measuredHeight);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //无黑边全屏
        int me = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(me,me);
        view.layout(0 ,0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if(bitmap==null){
            bitmap=loadBitmapFromView(view,false);
        }
        return bitmap;
    }

    /**
     * view转bitmap
     * @param v
     * @param isParemt
     * @return
     */
    public static Bitmap loadBitmapFromView(View v, boolean isParemt) {
        if (v == null) {
            return null;
        }
//      Bitmap  bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), HDConstantSet.BITMAP_QUALITY);
        Bitmap  bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.translate(-v.getScrollX(), -v.getScrollY());
        //c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return bitmap;
    }
    /**
     * 把View绘制到Bitmap上
     * @param comBitmap 需要绘制的View
     * @param width 该View的宽度
     * @param height 该View的高度
     * @return 返回Bitmap对象
     * add by csj 13-11-6
     */
    public static Bitmap getViewBitmap(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            comBitmap.layout(0, 0, width, height);

            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                DebugLog.e("failed getViewBitmap(" + comBitmap + ")");
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }

    /**
     * 把View绘制到Bitmap上
     * @param v
     * @return
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            DebugLog.e("failed getViewBitmap(" + v + ")");
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * 添加文字水印
     * @param src
     * @param content
     * @param textSize
     * @param color
     * @param x
     * @param y
     * @param recycle
     * @return
     */
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content, final float textSize, @ColorInt final int color, final float x, final float y, final boolean recycle) {
        if (src==null || content == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 添加图片水印
     * @param src
     * @param watermark
     * @param x
     * @param y
     * @param alpha
     * @param recycle
     * @return
     */
    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x,
                                           final int y,
                                           final int alpha,
                                           final boolean recycle) {
        if (src==null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        if (watermark!=null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 透明bitmap
     * @param src
     * @param recycle
     * @return
     */
    public static Bitmap toAlpha(final Bitmap src, final Boolean recycle) {
        if (src==null) return null;
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 灰色bitmap
     * @param src
     * @param recycle
     * @return
     */
    public static Bitmap toGray(final Bitmap src, final boolean recycle) {
        if (src==null) return null;
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 判断是否是图片
     * @param filePath
     * @return
     */
    public static boolean isImage(final String filePath) {
        String path = filePath.toUpperCase();
        return path.endsWith(".PNG") || path.endsWith(".JPG")
                || path.endsWith(".JPEG") || path.endsWith(".BMP")
                || path.endsWith(".GIF") || path.endsWith(".WEBP");
    }
    public static void main(String[] arg){

    }
}
