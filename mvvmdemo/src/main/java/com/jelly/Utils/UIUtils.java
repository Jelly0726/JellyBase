package com.jelly.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class UIUtils {
    //标准值  写在配置文件中  1920*1080
    private float yW=1080f;
    private float yH=1920f;
    //实际设备的宽高
     private float sW=0f;
     private float sH=0f;

     private String alss="com.android.internal.R$dimen";//
    private UIUtils(Context context){
        //得到设备实际宽高
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //定义一个
        DisplayMetrics displayMetrics=new DisplayMetrics();
        if (sW==0f ||sH==0f){
            w.getDefaultDisplay().getMetrics(displayMetrics);
            int syBarH=getSYHei(context);
        }
    }
    //反射得到状态栏高度
    private int getSYHei(Context context){
        return getValue(context,alss,"system_bar_height",48);
    }
    private int getValue(Context context,String  dime,String s_yBarH,int defultValue){
        try{
            Class<?> clz=Class.forName(dime);
            Object o=clz.newInstance();
            Field field=clz.getField(s_yBarH);
            int id=Integer.parseInt(field.get(o).toString());
            return context.getResources().getDimensionPixelOffset(id);
        }catch (Exception e){

        }
        return defultValue;
    }
}
