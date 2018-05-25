package com.base.screenCapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;

import com.base.Utils.ToastUtils;

/**
 * Created by Administrator on 2017/10/10.
 *  .只截取自己应用内部界面
 */

public class ScreenShot {
    private static ScreenShot screenShot;
    public static synchronized ScreenShot getInstance(){
        if (screenShot==null) {
            screenShot=new ScreenShot();
        }
        return screenShot;
    }
    private ScreenShot(){}
    /**
     * 截取整个屏幕包括状态栏
     * 只支持android 5.0以后
     * @param context
     */
    public void ScreenShot(Context context){
        Intent intent=new Intent(context, ScreenShotActivity.class);
        context.startActivity(intent);
    }
    /**
     * 截取某个控件或者区域
     * @param activity
     */
    public void ScreenShot(Activity activity){
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        boolean re= saveCroppedImage(activity,bitmap);
        if (re) {
            ToastUtils.showToast(activity, "成功保存到相册!");
        }else {
            ToastUtils.showToast(activity, "保存失败!");
        }
    }
    /**
     * 截取某个控件或者区域
     * @param dView
     */
    public void ScreenShot(Context context,View dView){
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        boolean re= saveCroppedImage(context,bitmap);
        if (re) {
            ToastUtils.showToast(context, "成功保存到相册!");
        }else {
            ToastUtils.showToast(context, "保存失败!");
        }
    }
    /*
* 保存Bitmap到sd卡中
*/
    private boolean saveCroppedImage(Context context,Bitmap bmp) {
//        File file = new File(Environment.getExternalStorageDirectory().getPath()
//                + "/"+System.currentTimeMillis()+".jpg");
        try {
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG,50, fos);
//            fos.flush();
//            fos.close();
//            if (bmp != null && !bmp.isRecycled()) {
//                bmp.recycle();
//            }
            //保存到系统相册
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "title", "description");
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(file);
//            intent.setData(uri);
//            //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
//            context.sendBroadcast(intent);
            return true;
        //} catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            return false;
        }
    }
}
