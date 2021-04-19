package com.jelly.baselibrary.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.jelly.baselibrary.cropper.CropperActivity;
import com.jelly.baselibrary.log.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;


/**
 * 图片工具类
 */
public class BitmapUtil {
    public static final int IMG_CROP = 99;//裁剪图片的请求码
    public static final String PATH = "path";//

    private BitmapUtil() {
    }

    private static BitmapUtil instance;

    public static synchronized BitmapUtil getInstance() {
        if (instance == null) {
            instance = new BitmapUtil();
        }
        return instance;
    }

    // 扫描的三种方式
    public static enum ScannerType {
        RECEIVER, MEDIA
    }

    // 首先保存图片(私有分区)
    public String saveImageToGallery(Context context, Bitmap bitmap, ScannerType type) {
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
            return file.getAbsolutePath();
        }
    }

    /**
     * 首先保存图片(私有分区)
     *
     * @param context
     * @param bitmap
     * @return
     */
    public String saveBitmap(Context context, Bitmap bitmap) {
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
            return file.getAbsolutePath();
        }
    }

    /**
     * 保存Bitmap到系统相册(共享分区)
     */
    public boolean saveImage(Context context, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 29) {
//            boolean isTrue = saveSignImage(bitName, bitmap);
            saveSignImage(context, bitmap);
            return true;
//            file= getPrivateAlbumStorageDir(NewPeoActivity.this, bitName,brand);
//            return isTrue;
        }
        String bitName = System.currentTimeMillis() + ".jpg";
        // 插入图库
        String uri = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, bitName, null);
        String realPath = getRealPathFromURI(Uri.parse(uri), context);//得到绝对地址
        if (!realPath.equals("")) {
            File file = new File(realPath);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(uri)));
        }
        return true;

    }

    //得到绝对地址,用于给图片标识时间,不然保存下来的图片会是1970年的陈年老图
    private String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String fileStr = cursor.getString(columnIndex);
            cursor.close();
            return fileStr;
        }
        return "";
    }

    /**
     * //将文件保存到公共的媒体文件夹
     * //这里的filepath不是绝对路径，而是某个媒体文件夹下的子路径，和沙盒子文件夹类似
     *
     * @param context
     * @param bitmap
     * @return
     */
    public void saveSignImage(/*String filePath,*/Context context, Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".jpg";
        try {
            //设置保存参数到ContentValues中
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/");
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
        }
    }
    /**
     * 保存图片到共享目录，不用SAF存储
     * @param context
     * @param bitmap  图片bitmap
     * @param fileName  图片名称
     * @param mime_type 类型：图片为image/jpeg，视频为video/mpeg
     */
    public boolean addToAlbum(Context context, Bitmap bitmap, String fileName,String mime_type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mime_type);
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri(Context context) {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }
    /**
     * 读取共享目录下图片文件
     * @param context  上下文
     * @param filename 文件名称（带后缀a.jpg），是MediaStore查找文件的条件之一
     * @return
     */
    public List<InputStream> getImageFile(Context context, String filename)  {
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Thumbnails.DATA
        };
        List<InputStream> insList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";//根据日期降序查询
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "='" + filename + "'";   //查询条件 “显示名称为？”
        Cursor cursor =  resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            //媒体数据库中查询到的文件id
            int columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            do {
                //通过mediaId获取它的uri
                int mediaId = cursor.getInt(columnId);
//                String tPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)); //获取图片路径
                Uri itemUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + mediaId );
                try {
                    //通过uri获取到inputStream
                    ContentResolver cr = context.getContentResolver();
                    InputStream ins=cr.openInputStream(itemUri);
                    insList.add(ins);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return insList;
    }
    /**
     * 读取共享目录下视频文件
     * @param context
     * @param filename 文件名称（带后缀a.mp4），是MediaStore查找文件的条件之一
     * @return
     */
    public List<InputStream> getVideoFile(Context context, String filename)  {
        String[] projection = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Thumbnails.DATA
        };
        List<InputStream> insList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        String sortOrder = MediaStore.Video.Media.DATE_MODIFIED + " DESC";//根据日期降序查询
        String selection = MediaStore.Video.Media.DISPLAY_NAME + "='" + filename + "'";   //查询条件 “显示名称为？”
        Cursor cursor =  resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            //媒体数据库中查询到的文件id
            int columnId = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            do {
                //通过mediaId获取它的uri
                int mediaId = cursor.getInt(columnId);
//                String tPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)); //获取图片路径
                Uri itemUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + mediaId );
                try {
                    //通过uri获取到inputStream
                    ContentResolver cr = context.getContentResolver();
                    InputStream ins=cr.openInputStream(itemUri);
                    insList.add(ins);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return insList;
    }

    /**
     * 将文件路径转为uri
     * @param context
     * @param path 文件路径
     * @return
     */
    public Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    /**
     * Receiver扫描更新图库图片
     **/

    private void ScannerByReceiver(Context context, String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + path)));
        LogUtils.v("TAG", "receiver scanner completed");
    }

    /**
     * MediaScanner 扫描更新图库图片
     **/

    private void ScannerByMedia(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
        LogUtils.v("TAG", "media scanner completed");
    }

    /**
     * 根据路径删除图片
     *
     * @param context
     * @param path
     */
    public void deleteFile(final Context context, final String path) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        String where = MediaStore.Images.Media.DATA + "='" + path + "'";
        //删除图片
        mContentResolver.delete(uri, where, null);
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(context, new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            context.sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            File file = new File(path);
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }

    /**
     * 打开图片裁剪
     *
     * @param context
     * @param path    原图片路径
     */
    public void startCropIntent(Activity context, String path) {
        File file = new File(path);
        Intent intent = new Intent(context, CropperActivity.class);
        Uri uri = Uri.fromFile(file);// parse(pathUri);13         intent.setDataAndType(uri, "image/*");
        intent.setData(uri);
        context.startActivityForResult(intent, IMG_CROP);
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 将byte[]转换成InputStream
     *
     * @param b
     * @return
     */
    public InputStream Byte2InputStream(byte[] b) {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }

    /**
     * 将InputStream转换成byte[]
     *
     * @param is
     * @return
     */
    public byte[] InputStream2Bytes(InputStream is) {
        String str = "";
        byte[] readByte = new byte[1024];
        int readCount = -1;
        try {
            while ((readCount = is.read(readByte, 0, 1024)) != -1) {
                str += new String(readByte).trim();
            }
            return str.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bm
     * @return
     */
    public InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bm
     * @param quality
     * @return
     */
    public InputStream Bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * 将InputStream转换成Bitmap
     *
     * @param is
     * @return
     */
    public Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    /**
     * Drawable转换成InputStream
     *
     * @param d
     * @return
     */
    public InputStream Drawable2InputStream(Drawable d) {
        Bitmap bitmap = this.drawable2Bitmap(d);
        return this.Bitmap2InputStream(bitmap);
    }

    /**
     * InputStream转换成Drawable
     *
     * @param is
     * @return
     */
    public Drawable InputStream2Drawable(Context context,InputStream is) {
        Bitmap bitmap = this.InputStream2Bitmap(is);
        return this.bitmap2Drawable(context,bitmap);
    }

    /**
     * bitmap转字节数组
     *
     * @param bitmap
     * @param format
     * @return
     */
    public byte[] bitmap2Bytes(final Bitmap bitmap, final Bitmap.CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 字节数组转成bitmap
     *
     * @param bytes
     * @return
     */
    public Bitmap bytes2Bitmap(final byte[] bytes) {
        return (bytes == null || bytes.length == 0)
                ? null
                : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * drawable转成bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawable2Bitmap(final Drawable drawable) {
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
                            ? Config.ARGB_8888
                            : Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Config.ARGB_8888
                            : Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转成Drawable
     *
     * @param bitmap
     * @return
     */
    public Drawable bitmap2Drawable(Context context,final Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(context.getApplicationContext().getResources(), bitmap);
    }

    /**
     * drawable转字节数组
     *
     * @param drawable
     * @param format
     * @return
     */
    public byte[] drawable2Bytes(final Drawable drawable, final Bitmap.CompressFormat format) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /**
     * 字节数组转 drawable
     *
     * @param bytes
     * @return
     */
    public Drawable bytes2Drawable(Context context,final byte[] bytes) {
        return bitmap2Drawable(context,bytes2Bitmap(bytes));
    }

    // 通过uri获取bitmap
    public Bitmap getBitmapFromUri(Context context, Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileDescriptor fileDescriptor = null;
        Bitmap bitmap = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null && parcelFileDescriptor.getFileDescriptor() != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                //转换uri为bitmap类型
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
            }
            return bitmap;
        }
    }

    /**
     * 根据文件路径获取bitmap
     *
     * @param file 文件路径
     * @return bitmap
     */
    public Bitmap getBitmapByFile(File file) {
        if (file == null) return null;
        return getBitmapByFile(file.getPath());
    }

    /**
     * 根据文件路径获取bitmap
     *
     * @param filePath 文件路径
     * @return bitmap
     */
    public Bitmap getBitmapByFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 根据文件路径获取bitmap
     *
     * @param res 资源对象
     * @param id  资源id
     * @return bitmap
     */
    public Bitmap getBitmapByResource(Resources res, int id) {
        return BitmapFactory.decodeResource(res, id);
    }

    /**
     * @param filePath 文件路径
     * @return bitmap
     */
    public Bitmap getBitmapByFile(String filePath, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(filePath)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 根据路径将图片压缩后并转base64
     *
     * @param filePath 文件路径
     * @return bitmap
     */
    public String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * 缩放图片
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    public Bitmap scaleImage(Bitmap src, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
    }

    /**
     * 缩放图片
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度比
     * @param scaleHeight 缩放高度比
     * @return 缩放后的图片
     */
    public Bitmap scaleImage(Bitmap src, float scaleWidth, float scaleHeight) {
        if (src == null) return null;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap res = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (!src.isRecycled()) src.recycle();
        return res;
    }

    /**
     * 设置bitmap的宽高
     *
     * @param bm
     * @param newWidth  为0则以高为准
     * @param newHeight 为0则以宽为准
     * @return
     */
    public Bitmap zoomImg(Bitmap bm, float newWidth, float newHeight) {
        if (newWidth <= 0 && newHeight <= 0) return bm;
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = 0f;
        float scaleHeight = 0f;
        /**
         * height/width=newHeight/newWidth
         * newHeight=newWidth*(height/width);
         * newWidth=newHeight/(height/width);
         */
        if (newWidth > 0 && newHeight > 0) {//判断以哪边为准
            scaleWidth = newWidth / (float) width;
            scaleHeight = newHeight / (float) height;
        } else if (newWidth > 0) {//以宽为准 高通过原宽高比计算
            newHeight = (newWidth * ((float) height / (float) width));
            scaleWidth = newWidth / (float) width;
            scaleHeight = newHeight / (float) height;
        } else if (newHeight > 0) {//以高为准 宽通过原宽高比计算
            newWidth = (newHeight / ((float) height / (float) width));
            scaleWidth = newWidth / (float) width;
            scaleHeight = newHeight / (float) height;
        }
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 旋转图片
     *
     * @param src     源图片
     * @param degrees 旋转角度
     */
    public Bitmap rotateBitmap(Bitmap src, int degrees) {
        if (src == null || degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, src.getWidth() / 2, src.getHeight() / 2);
        Bitmap res = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (!src.isRecycled()) src.recycle();
        return res;
    }

    /**
     * 获取图片旋转角度
     *
     * @param path 路径
     */
    public int getRotateDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                default:
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 转为圆形图片
     *
     * @param src 源图片
     * @return 圆形图片
     */
    public Bitmap toRound(Bitmap src) {
        if (src == null) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);
        if (!src.isRecycled()) src.recycle();
        return output;
    }

    /**
     * 转为圆角图片
     *
     * @param src 源图片
     * @param ret 圆角的度数
     * @return 圆角图片
     */
    public Bitmap toRoundCorner(Bitmap src, float ret) {
        if (null == src) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap out = Bitmap.createBitmap(width, height,
                Config.ARGB_8888);
        BitmapShader bitmapShader = new BitmapShader(src,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        RectF rectf = new RectF(0, 0, width, height);
        Canvas canvas = new Canvas(out);
        canvas.drawRoundRect(rectf, ret, ret, paint);
        canvas.save();
        canvas.restore();
        if (!src.isRecycled()) src.recycle();
        return out;
    }

    /**
     * 快速模糊
     * <p>先缩小原图，对小图进行模糊，再放大回原先尺寸</p>
     *
     * @param context 上下文
     * @param src     源图片
     * @param scale   缩小倍数
     * @param radius  模糊半径
     * @return 模糊后的图片
     */
    public Bitmap fastBlur(Context context, Bitmap src, int scale, float radius) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int scaleWidth = width / scale;
        int scaleHeight = height / scale;
        if (scaleWidth == 0 || scaleHeight == 0) return null;
        Bitmap scaled = Bitmap.createBitmap(scaleWidth, scaleHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(scaled);
        canvas.scale(1 / (float) scale, 1 / (float) scale);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        PorterDuffColorFilter filter = new PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (!src.isRecycled()) src.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            scaled = renderScriptBlur(context, scaled, radius);
        } else {
            scaled = stackBlur(scaled, (int) radius, true);
        }
        if (scale == 1) return scaled;
        Bitmap res = Bitmap.createScaledBitmap(scaled, width, height, true);
        if (scaled != null && !scaled.isRecycled()) scaled.recycle();
        return res;
    }

    /**
     * renderScript模糊图片
     * <p>API大于17</p>
     *
     * @param context 上下文
     * @param src     源图片
     * @param radius  模糊度(0...25)
     * @return 模糊后的图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap renderScriptBlur(Context context, Bitmap src, float radius) {
        if (isEmptyBitmap(src)) return null;
        RenderScript rs = null;
        try {
            rs = RenderScript.create(context);
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs, src, Allocation.MipmapControl.MIPMAP_NONE, Allocation
                    .USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            if (radius > 25) {
                radius = 25.0f;
            } else if (radius <= 0) {
                radius = 1.0f;
            }
            blurScript.setInput(input);
            blurScript.setRadius(radius);
            blurScript.forEach(output);
            output.copyTo(src);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
        return src;
    }

    /**
     * stack模糊图片
     *
     * @param src              源图片
     * @param radius           模糊半径
     * @param canReuseInBitmap 是否回收
     * @return stackBlur模糊图片
     */
    public Bitmap stackBlur(Bitmap src, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = src;
        } else {
            bitmap = src.copy(src.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 添加颜色边框
     *
     * @param src         源图片
     * @param borderWidth 左右边框宽度
     * @param borderWidth 上下边框宽度
     * @param color       边框的颜色值
     * @return 带颜色边框图
     */
    public Bitmap addFrame(Bitmap src, int borderWidth, int borderHeight, int color) {
        if (isEmptyBitmap(src)) return null;
        int newWidth = src.getWidth() + borderWidth;
        int newHeight = src.getHeight() + borderHeight;
        Bitmap out = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        Rect rec = new Rect();
        rec.bottom = newHeight--;
        rec.right = newWidth--;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawRect(rec, paint);
        canvas.drawBitmap(src, borderWidth / 2, borderHeight / 2, null);
        canvas.save();
        canvas.restore();
        if (!src.isRecycled()) src.recycle();
        return out;
    }

    /**
     * 添加倒影
     *
     * @param src              源图片的
     * @param reflectionHeight 倒影高度
     * @return 倒影图
     */
    public Bitmap addReflection(Bitmap src, int reflectionHeight) {
        if (isEmptyBitmap(src)) return null;
        final int REFLECTION_GAP = 0;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        if (0 == srcWidth || srcHeight == 0) return null;
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false);
        if (null == reflectionBitmap) return null;
        Bitmap out = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight,
                Config.ARGB_8888);
        if (null == out) return null;
        Canvas canvas = new Canvas(out);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        LinearGradient shader = new LinearGradient(0, srcHeight, 0,
                out.getHeight() + REFLECTION_GAP,
                0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.DST_IN));
        canvas.save();
        canvas.drawRect(0, srcHeight, srcWidth,
                out.getHeight() + REFLECTION_GAP, paint);
        canvas.restore();
        if (!src.isRecycled()) src.recycle();
        if (!reflectionBitmap.isRecycled()) reflectionBitmap.recycle();
        return out;
    }

    /**
     * VectorDrawable 转为 Bitmap
     *
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
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * view转bitmap (view的布局会发生改变)
     *
     * @param view
     * @return
     */
    public Bitmap view2Bitmap(Activity activity, final View view) {
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
        Bitmap bitmap1 = view.getDrawingCache();
        if (bitmap1 == null) {
            bitmap1 = loadBitmapFromView(view, false);
        }
        Bitmap bitmap = Bitmap.createBitmap(bitmap1); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }

    /**
     * view转bitmap
     *
     * @param activity
     * @param layoutId
     * @return
     */
    public Bitmap getViewBitmap(Activity activity, int layoutId) {
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
        view.measure(me, me);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            bitmap = loadBitmapFromView(view, false);
        }
        return bitmap;
    }

    /**
     * view转bitmap
     *
     * @param v
     * @param isParemt
     * @return
     */
    public Bitmap loadBitmapFromView(View v, boolean isParemt) {
        if (v == null) {
            return null;
        }
//      Bitmap  bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), HDConstantSet.BITMAP_QUALITY);
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Config.ARGB_8888);
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
     *
     * @param comBitmap 需要绘制的View
     * @param width     该View的宽度
     * @param height    该View的高度
     * @return 返回Bitmap对象
     * add by csj 13-11-6
     */
    public Bitmap getViewBitmap(View comBitmap, int width, int height) {
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
                LogUtils.e("failed getViewBitmap(" + comBitmap + ")");
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
     *
     * @param v
     * @return
     */
    public Bitmap getViewBitmap(View v) {
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
            LogUtils.e("failed getViewBitmap(" + v + ")");
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
     * 新建Bitmap，将View中内容绘制到Bitmap上
     *
     * @param view
     * @return
     */
    public Bitmap createBitmapFromView(View view) {
        //是ImageView直接获取
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            canvas.setBitmap(null);
        }
        return bitmap;
    }

    /**
     * 该方式原理主要是：View组件显示的内容可以通过cache机制保存为bitmap
     */
    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = null;
        //开启view缓存bitmap
        view.setDrawingCacheEnabled(true);
        //设置view缓存Bitmap质量
        view.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        //获取缓存的bitmap
        Bitmap cache = view.getDrawingCache();
        if (cache != null && !cache.isRecycled()) {
            bitmap = Bitmap.createBitmap(cache);
        }
        //销毁view缓存bitmap
        view.destroyDrawingCache();
        //关闭view缓存bitmap
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 添加文字水印
     *
     * @param src      源图片
     * @param content  文本
     * @param textSize 字体大小
     * @param color    颜色
     * @param x        起始坐标x
     * @param y        起始坐标y
     * @param recycle  是否回收
     * @return
     */
    public Bitmap addTextWatermark(final Bitmap src,
                                   final String content, final float textSize, @ColorInt final int color, final float x, final float y, final boolean recycle) {
        if (src == null || content == null) return null;
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
     *
     * @param src       源图片
     * @param watermark 水印图片
     * @param x         起始坐标x
     * @param y         起始坐标y
     * @param recycle   是否回收
     * @return
     */
    public Bitmap addImageWatermark(final Bitmap src,
                                    final Bitmap watermark,
                                    final int x,
                                    final int y,
                                    final int alpha,
                                    final boolean recycle) {
        if (src == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        if (watermark != null) {
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
     *
     * @param src
     * @param recycle
     * @return
     */
    public Bitmap toAlpha(final Bitmap src, final Boolean recycle) {
        if (src == null) return null;
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 转为灰度图像
     *
     * @param src 源图片
     * @return 灰度图
     */
    public Bitmap toGray(Bitmap src) {
        return toGray(src, false);
    }

    /**
     * 灰色bitmap
     *
     * @param src
     * @param recycle
     * @return
     */
    public Bitmap toGray(final Bitmap src, final boolean recycle) {
        if (src == null) return null;
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
     * 设置图片大小
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public Drawable getDrawable(Context context,int src, int width, int height) {
        Drawable otherDrawable = context.getApplicationContext().getResources().getDrawable(src);
        otherDrawable.setBounds(0, 0, AppUtils.dipTopx(context.getApplicationContext(), width)
                , AppUtils.dipTopx(context.getApplicationContext(), height));//第一0是距左边距离，第二0是距上边距离，30、35分别是长宽
        return otherDrawable;
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param file 　文件
     */
    public boolean isImage(File file) {
        return file != null && isImage(file.getPath());
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param filePath 　文件路径
     */
    public boolean isImage(String filePath) {
        String path = filePath.toUpperCase();
        return path.endsWith(".PNG") || path.endsWith(".JPG")
                || path.endsWith(".JPEG") || path.endsWith(".BMP")
                || path.endsWith(".GIF");
    }


    /**
     * 获取图片类型
     *
     * @param filePath 图片路径
     * @return 图片类型
     */
    public String getImageType(String filePath) {
        String path = filePath.toUpperCase();
        if (path.endsWith(".PNG")) {
            return "image/png";
        } else if (path.endsWith(".JPG")) {
            return "image/jpeg";
        } else if (path.endsWith(".JPEG")) {
            return "image/jpeg";
        } else if (path.endsWith(".BMP")) {
            return "image/bmp";
        } else if (path.endsWith(".GIF")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    /**
     * 流获取图片类型
     *
     * @param is 图片输入流
     * @return 图片类型
     */
    public String getImageType(InputStream is) {
        if (is == null) return null;
        try {
            byte[] bytes = new byte[8];
            return is.read(bytes, 0, 8) != -1 ? getImageType(bytes) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取图片类型
     *
     * @param bytes bitmap的前8字节
     * @return 图片类型
     */
    public String getImageType(byte[] bytes) {
        if (isJPEG(bytes)) return "JPEG";
        if (isGIF(bytes)) return "GIF";
        if (isPNG(bytes)) return "PNG";
        if (isBMP(bytes)) return "BMP";
        return null;
    }

    private boolean isJPEG(byte[] b) {
        return b.length >= 2
                && (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private boolean isGIF(byte[] b) {
        return b.length >= 6
                && b[0] == 'G' && b[1] == 'I'
                && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private boolean isPNG(byte[] b) {
        return b.length >= 8
                && (b[0] == (byte) 137 && b[1] == (byte) 80
                && b[2] == (byte) 78 && b[3] == (byte) 71
                && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private boolean isBMP(byte[] b) {
        return b.length >= 2
                && (b[0] == 0x42) && (b[1] == 0x4d);
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
    /**
     * 获取图片宽高
     *
     * @param urlStr
     * @param callBack
     */
    public void getImageSize(@NonNull String urlStr, GetImageSizeCallBack callBack) {
        if (urlStr.toUpperCase().startsWith("HTTP")
                ||urlStr.toUpperCase().startsWith("HTTPS")){
            new GetImageSizeTask(urlStr, callBack);
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //bitmap.options类为bitmap的裁剪类，通过他可以实现bitmap的裁剪；
        // 如果不设置裁剪后的宽高和裁剪比例，返回的bitmap对象将为空，但是这个对象存储了原bitmap的宽高信息，bitmap对象为空不会引发OOM。
        Bitmap bitmap = BitmapFactory.decodeFile(urlStr, options);
        int width = options.outWidth;
        int height = options.outHeight;
        if (callBack != null)
            callBack.GetSize(width, height);


    }

    /**
     * 获取图片宽高
     *
     * @param urlStr
     * @param callBack
     */
    public void getImageSize(@NonNull Context context, int urlStr, GetImageSizeCallBack callBack) {
        Resources res = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(res, urlStr, options);
        //bitmap.options类为bitmap的裁剪类，通过他可以实现bitmap的裁剪；
        // 如果不设置裁剪后的宽高和裁剪比例，返回的bitmap对象将为空，但是这个对象存储了原bitmap的宽高信息，bitmap对象为空不会引发OOM。
//        Bitmap bitmap = BitmapFactory.decodeFile(src, options);
        int width = options.outWidth;
        int height = options.outHeight;
        if (callBack != null)
            callBack.GetSize(width, height);
    }

    private class GetImageSizeTask extends AsyncTask<Void, Void, Boolean> {
        private GetImageSizeCallBack callBack;
        private String urlStr;
        private int width, height;

        public GetImageSizeTask(String urlStr, GetImageSizeCallBack callBack) {
            this.callBack = callBack;
            this.urlStr = urlStr;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (urlStr == null || urlStr.length() <= 0) return false;
            boolean result = false;
            try {
                URL mUrl = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                InputStream is = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);
                width = options.outWidth;
                height = options.outHeight;
                result = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                if (callBack != null)
                    callBack.GetSize(width, height);
            } else {
                if (callBack != null)
                    callBack.GetSize(0, 0);
            }
        }

    }

    /**
     * 获取图片宽高回调
     */
    public interface GetImageSizeCallBack {
        public void GetSize(float width, float height);
    }
    public void main(String[] arg) {

    }
}
