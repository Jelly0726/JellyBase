package com.base.screenCapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import com.base.appManager.MyApplication;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;


/**
 * Created by wei on 16-12-1.
 */
public class Shotter {

    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private String mLocalUrl = "";

    private OnShotListener mOnShotListener;


    public Shotter(Context context, Intent data) {
        this.mRefContext = new SoftReference<>(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK,
                    data);

            mImageReader = ImageReader.newInstance(
                    getScreenWidth(),
                    getScreenHeight(),
                    PixelFormat.RGB_565,// a pixel两节省一些内存 个2个字节 此处RGB_565 必须和下面 buffer处理一致的格式
                    1);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                getScreenWidth(),
                getScreenHeight(),
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);

    }

    public void startScreenShot(OnShotListener onShotListener, String loc_url) {
        mLocalUrl = loc_url;
        startScreenShot(onShotListener);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScreenShot(OnShotListener onShotListener) {

        mOnShotListener = onShotListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            virtualDisplay();

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Image image = mImageReader.acquireLatestImage();

                                        AsyncTaskCompat.executeParallel(new SaveTask(), image);
                                    }
                                },
                    300);

        }

    }


    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.RGB_565);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
//                try {
//                    if (TextUtils.isEmpty(mLocalUrl)) {
//                        File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/"+System.currentTimeMillis()+".jpg");
//                        mLocalUrl = fileUri.getPath();
////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
////                            mLocalUrl = FileProvider.getUriForFile(MyApplication.getMyApp(),
////                                    MyApplication.getMyApp().getPackageName()+".fileprovider", fileUri).getPath();//通过FileProvider创建一个content类型的Uri
////                        mLocalUrl = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
////                                +
////                                "/"
////                                +
////                                SystemClock.currentThreadTimeMillis() + ".png";
//                    }
//                    fileImage = new File(mLocalUrl);
//                    if (!fileImage.exists()) {
//                        fileImage.createNewFile();
//                    }
//                    FileOutputStream out = new FileOutputStream(fileImage);
//                    if (out != null) {
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                        out.flush();
//                        out.close();
//                    }
                    //保存到系统相册
                    MediaStore.Images.Media.insertImage(MyApplication.getMyApp().getContentResolver(), bitmap, "title", "description");
//                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    Uri uri = Uri.fromFile(fileImage);
//                    intent.setData(uri);
//                    //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
//                    MyApplication.getMyApp().sendBroadcast(intent);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    fileImage = null;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    fileImage = null;
//                }
            }

            if (fileImage != null) {
                return bitmap;
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mOnShotListener != null) {
                if(bitmap != null){
                    mOnShotListener.onFinish("截屏成功！");
                }else {
                    mOnShotListener.onFinish("截屏失败！");
                }
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }

        }
    }


    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }


    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    // a  call back listener
    public interface OnShotListener {
        void onFinish(String msg);
    }
}