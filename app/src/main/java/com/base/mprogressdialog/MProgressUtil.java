package com.base.mprogressdialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;

/**
 * Created by Administrator on 2017/11/12.
 */

public class MProgressUtil {
    private volatile static MProgressUtil sInstance;
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
    public MProgressDialog getMProgressDialog(Context context){
        //新建一个Dialog
        MProgressDialog  progressDialog = new MProgressDialog.Builder(context)
                //点击外部是否可以取消
                .isCanceledOnTouchOutside(false)
                //全屏背景窗体的颜色
                .setBackgroundWindowColor(ContextCompat.getColor(context, R.color.BackgroundWindowColor))
                //View背景的颜色
                .setBackgroundViewColor(ContextCompat.getColor(context,R.color.BackgroundViewColor))
                //View背景的圆角
                .setCornerRadius(context.getResources().getDimension(R.dimen.CornerRadius))
                //View 边框的颜色
                .setStrokeColor(ContextCompat.getColor(context,R.color.StrokeColor))
                //View 边框的宽度
                .setStrokeWidth(context.getResources().getDimension(R.dimen.StrokeWidth))
                //Progress 颜色
                .setProgressColor(ContextCompat.getColor(context,R.color.ProgressColor))
                //Progress 宽度
                .setProgressWidth(context.getResources().getDimension(R.dimen.ProgressWidth))
                //Progress 内圈颜色
                .setProgressRimColor(Color.TRANSPARENT)
                //Progress 内圈宽度
                .setProgressRimWidth(context.getResources().getDimensionPixelSize(R.dimen.ProgressRimWidth))
                //文字的颜色
                .setTextColor(ContextCompat.getColor(context,R.color.TextColor))
                //取消的监听
                .setOnDialogDismissListener(new MProgressDialog.OnDialogDismissListener() {
                    @Override
                    public void dismiss() {
                        //mHandler.removeCallbacksAndMessages(null);
                        //MToast.makeTextShort(mContext, "Dialog消失了").show();
                    }
                })
                .build();
        return progressDialog;
    }
}
