package com.base.toast;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jelly.jellybase.R;


/**
 * Created by Administrator on 2017/2/16.
 */

public class ToastUtils {
    /**
     * 默认Toast样式
     * @param context
     * @param msg
     */
    public static void show(@Nullable Context context, @Nullable String msg){
        Toast.makeText(context.getApplicationContext(),msg,
                Toast.LENGTH_SHORT).show();
    }
    /**
     * 默认Toast样式
     * @param context
     * @param msg
     */
    public static void show(@Nullable Context context, @Nullable int msg){
        Toast.makeText(context.getApplicationContext(),msg,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 屏幕居中显示Toast
     * @param context
     * @param msg
     */
    public static void showToast(@Nullable Context context, @Nullable String msg){
        Toast toast = Toast.makeText(context.getApplicationContext(),
                msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 屏幕居中显示Toast
     * @param context
     * @param msg
     */
    public static void showToast(@Nullable Context context, @Nullable int msg){
        Toast toast = Toast.makeText(context.getApplicationContext(),
                msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 屏幕居中显示Toast短时
     * @param context
     * @param msg
     */
    public static void showShort(@Nullable Context context, @Nullable String msg){
        Toast toast = Toast.makeText(context.getApplicationContext(),
                msg,  Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 屏幕居中显示Toast短时
     * @param context
     * @param msg
     */
    public static void showShort(@Nullable Context context, @Nullable int msg){
        Toast toast = Toast.makeText(context.getApplicationContext(),
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 屏幕居中显示带图片的Toast
     * @param context
     * @param msg
     * @param icon
     */
    public static void showIconToast(@Nullable Context context, @Nullable String msg, @Nullable int icon){
        Toast toast = Toast.makeText(context.getApplicationContext(),
                msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(context.getApplicationContext());
        imageCodeProject.setImageResource(icon);
        toastView.addView(imageCodeProject, 0);
        toast.show();
    }
    /**
     * 屏幕居中显示带图片的Toast
     * @param context
     * @param msg
     * @param icon
     */
    public static void showIconToast(@Nullable Context context, @Nullable int msg, @Nullable int icon){
        Toast toast = Toast.makeText(context.getApplicationContext(),
                msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(context.getApplicationContext());
        imageCodeProject.setImageResource(icon);
        toastView.addView(imageCodeProject, 0);
        toast.show();
    }

    /**
     * 屏幕居中显示自定义布局toast
     * @param activity
     * @param msg
     * @param titless
     * @param icon
     */
    public static void showIconToast(@Nullable Activity activity, @Nullable String msg
            , @Nullable String titless, @Nullable int icon){
        LayoutInflater inflater =activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.toast, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);
        image.setBackgroundResource(icon);
        title.setText(titless);
        content.setText(msg);
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
    /**
     * 屏幕居中显示自定义布局toast
     * @param activity
     * @param msg
     * @param titless
     * @param icon
     */
    public static void showIconToast(@Nullable Activity activity, @Nullable int msg
            , @Nullable int titless, @Nullable int icon){
        LayoutInflater inflater =activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.toast, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);
        image.setBackgroundResource(icon);
        title.setText(titless);
        content.setText(msg);
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
