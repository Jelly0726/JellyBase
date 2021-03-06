package com.jelly.baselibrary.toast;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * 核心toast类
 *
 * @author lin
 */

public class ToastUtils {
    private static final int NOTIFICATION_UNKNOWN = -1;
    private static final int NOTIFICATION_DISABLED = 0;
    private static final int NOTIFICATION_ENABLED = 1;
    private static int sNotificationStatus = NOTIFICATION_UNKNOWN;

    public static final int UNIVERSAL = 0;
    public static final int EMPHASIZE = 1;
    public static final int CLICKABLE = 2;
    public static final int CUSTOM = 3;

    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

    @IntDef({UNIVERSAL, EMPHASIZE, CLICKABLE,CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @IntDef({LENGTH_LONG, LENGTH_SHORT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }
    /**
     * 默认Toast样式
     * @param context
     * @param msg
     */
    public static void show(@Nullable Context context, @Nullable String msg){
        makeText(context,msg, LENGTH_LONG,UNIVERSAL).show();
    }
    /**
     * 默认Toast样式
     * @param context
     * @param msg
     */
    public static void show(@Nullable Context context, @Nullable int msg){
        makeText(context,msg, LENGTH_LONG,UNIVERSAL).show();
    }

    /**
     * 屏幕居中显示Toast
     * @param context
     * @param msg
     */
    public static void showToast(@Nullable Context context, @Nullable String msg){
        makeText(context,msg, LENGTH_LONG,UNIVERSAL)
                .setGravity( Gravity.CENTER, 0, 0).show();
    }
    /**
     * 屏幕居中显示Toast
     * @param context
     * @param msg
     */
    public static void showToast(@Nullable Context context, @Nullable int msg){
        makeText(context,msg, LENGTH_LONG,UNIVERSAL)
                .setGravity( Gravity.CENTER, 0, 0).show();
    }
    /**
     * 屏幕居中显示Toast短时
     * @param context
     * @param msg
     */
    public static void showShort(@Nullable Context context, @Nullable String msg){
        makeText(context,msg, LENGTH_SHORT,UNIVERSAL)
                .setGravity( Gravity.CENTER, 0, 0).show();
    }
    /**
     * 屏幕居中显示Toast短时
     * @param context
     * @param msg
     */
    public static void showShort(@Nullable Context context, @Nullable int msg){
        makeText(context,msg, LENGTH_SHORT,UNIVERSAL)
                .setGravity( Gravity.CENTER, 0, 0).show();
    }
    public static IToast makeText(@NonNull Context context, @NonNull int text) {
        return makeText(context, context.getString(text), LENGTH_SHORT, UNIVERSAL);
    }
    public static IToast makeText(@NonNull Context context, @NonNull String text) {
        return makeText(context,text, LENGTH_SHORT, UNIVERSAL);
    }
    public static IToast makeText(@NonNull Context context, @NonNull int text, @Duration int duration,@Type int
            type) {
        return makeText(context, context.getString(text), duration, type);
    }
    public static IToast makeText(@NonNull Context context, @NonNull int text, @Duration int duration) {
        return makeText(context, context.getString(text), duration, UNIVERSAL);
    }
    public static IToast makeText(@NonNull Context context, @NonNull String text, @Duration int duration) {
        return makeText(context, text, duration, UNIVERSAL);
    }

    public static IToast makeText(@NonNull Context context, @NonNull String text, @Duration int duration, @Type int
            type) {
        // 允许通知权限,并且不需要点击则用系统toast
        // 没有通知权限或者是可点击的toast则使用自定义toast
        if (notificationEnabled(context) && type != CLICKABLE && type!=CUSTOM) {
            Log.d("TAG", sNotificationStatus + ":SystemToast");
            return SystemToast.makeText(context, text, duration, type);
        } else {
            Log.d("TAG", sNotificationStatus + ":CustomToast");
            return CustomToast.makeText(context, text, duration, type);
        }
    }

    private static boolean notificationEnabled(Context context) {
        // 5.0以下采用自定义toast
        if (sNotificationStatus == NOTIFICATION_UNKNOWN) {
            if (Build.VERSION.SDK_INT >= KITKAT) {
                sNotificationStatus = NotificationManagerCompat.from(context).areNotificationsEnabled() ?
                        NOTIFICATION_ENABLED : NOTIFICATION_DISABLED;
            } else {
                sNotificationStatus = NOTIFICATION_DISABLED;
            }
        }
        return sNotificationStatus == NOTIFICATION_ENABLED;
    }
}

