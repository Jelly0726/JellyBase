package com.jelly.mprogressdialog;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.jelly.mvvmdemo.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.maning.mndialoglibrary.listeners.OnDialogDismissListener;

/**
 * Created by Administrator on 2017/11/12.
 */

public class MProgressUtil {
    private volatile static MProgressUtil sInstance;
    private MDialogConfig mDialogConfig;
    private OnDialogDismissListener onDialogDismissListener;
    private MProgressUtil(){

    }
    //获取单例
    public static MProgressUtil getInstance(){
        if (sInstance == null){
            synchronized (MProgressUtil.class){
                if (sInstance == null){
                    sInstance = new MProgressUtil();
                    return sInstance;
                }
            }
        }
        return sInstance;
    }
    public void initialize(Context context){
        //新建一个Dialog
//        MProgressDialog  progressDialog = new MProgressDialog.Builder(context)
//                //点击外部是否可以取消
//                .isCanceledOnTouchOutside(false)
//                //全屏背景窗体的颜色
//                .setBackgroundWindowColor(ContextCompat.getColor(context, R.color.BackgroundWindowColor))
//                //View背景的颜色
//                .setBackgroundViewColor(ContextCompat.getColor(context,R.color.BackgroundViewColor))
//                //View背景的圆角
//                .setCornerRadius(context.getResources().getDimension(R.dimen.CornerRadius))
//                //View 边框的颜色
//                .setStrokeColor(ContextCompat.getColor(context,R.color.StrokeColor))
//                //View 边框的宽度
//                .setStrokeWidth(context.getResources().getDimension(R.dimen.StrokeWidth))
//                //Progress 颜色
//                .setProgressColor(ContextCompat.getColor(context,R.color.ProgressColor))
//                //Progress 宽度
//                .setProgressWidth(context.getResources().getDimension(R.dimen.ProgressWidth))
//                //Progress 内圈颜色
//                .setProgressRimColor(Color.TRANSPARENT)
//                //Progress 内圈宽度
//                .setProgressRimWidth(context.getResources().getDimensionPixelSize(R.dimen.ProgressRimWidth))
//                //文字的颜色
//                .setTextColor(ContextCompat.getColor(context,R.color.TextColor))
//                //取消的监听
//                .setOnDialogDismissListener(new MProgressDialog.OnDialogDismissListener() {
//                    @Override
//                    public void dismiss() {
//                        //mHandler.removeCallbacksAndMessages(null);
//                        //MToast.makeTextShort(mContext, "Dialog消失了").show();
//                    }
//                })
//                .build();
        if (mDialogConfig==null)
            mDialogConfig = new MDialogConfig.Builder()
                    //全屏模式
                    .isWindowFullscreen(true)
                    //Progress大小（宽高）
                    .setProgressSize(60)
                    //点击外部是否可以取消
                    .isCanceledOnTouchOutside(false)
                    //物理返回键可以取消
                    .isCancelable(false)
                    //全屏背景窗体的颜色
                    .setBackgroundWindowColor(ContextCompat.getColor(context.getApplicationContext(), R.color.BackgroundWindowColor))
                    //View背景的颜色
                    .setBackgroundViewColor(ContextCompat.getColor(context.getApplicationContext(),R.color.BackgroundViewColor))
                    //View背景的圆角
                    .setCornerRadius(context.getResources().getDimension(R.dimen.CornerRadius))
                    //View 边框的颜色
                    .setStrokeColor(ContextCompat.getColor(context.getApplicationContext(),R.color.StrokeColor))
                    //View 边框的宽度
                    //View 边框的宽度
                    .setStrokeWidth(context.getResources().getDimension(R.dimen.StrokeWidth))
                    //Progress 颜色
                    .setProgressColor(ContextCompat.getColor(context.getApplicationContext(),R.color.ProgressColor))
                    //Progress 宽度
                    .setProgressWidth(context.getResources().getDimension(R.dimen.ProgressWidth))
                    //Progress 内圈颜色
                    .setProgressRimColor(ContextCompat.getColor(context.getApplicationContext(),R.color.transparent))
                    //Progress 内圈宽度
                    .setProgressRimWidth((int) context.getResources().getDimension(R.dimen.ProgressRimWidth))
                    //文字的颜色
                    .setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.TextColor))
                    //文字的大小
                    .setTextSize(15)
                    //ProgressBar 颜色
                    .setProgressColor(ContextCompat.getColor(context.getApplicationContext(),R.color.ProgressColor))
                    //dialog动画
//                .setAnimationID(R.style.animate_dialog_custom)
                    //padding
                    .setPadding(20, 20, 20, 20)
                    //关闭的监听
                    .setOnDialogDismissListener(onDialogDismissListener)
                    .build();
//        //默认
//        MProgressDialog.showProgress(this);
//        //自定义文字
//        MProgressDialog.showProgress(this,"自定义文字");
//        //不需要文字
//        MProgressDialog.showProgress(this,"");
    }
    public void show(Context context){
        show(context,"");
    }
    public void show(Context context,String text){
        MProgressDialog.showProgress(context,text,mDialogConfig);
    }
    public void dismiss(){
        MProgressDialog.dismissProgress();
    }
    public boolean isShowing(){
       return MProgressDialog.isShowing();
    }
    public OnDialogDismissListener getDismissListener() {
        return onDialogDismissListener;
    }

    public void setDismissListener(OnDialogDismissListener onDialogDismissListener) {
        this.onDialogDismissListener = onDialogDismissListener;
    }
}
