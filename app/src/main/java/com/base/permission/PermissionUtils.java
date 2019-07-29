package com.base.permission;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.base.appManager.BaseApplication;
import com.jelly.jellybase.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Administrator on 2018/1/24.
 */

public class PermissionUtils {
    //设置请求码
    public static final int REQUEST_CODE_SETTING = 300;
    public static final int RC_PERMISSION_MANAGE_OVERLAY_PERMISSION = 301;
    private static final String SHOW_DOZE_ALERT_KEY = "SHOW_DOZE_ALERT_KEY";
    private PermissionUtils(){
    }
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final PermissionUtils instance=new PermissionUtils();
    }
    /**
     * 单一实例
     */
    public static PermissionUtils getInstance(){
        return PermissionUtils.SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    public Object readResolve() throws ObjectStreamException {
        return PermissionUtils.SingletonHolder.instance;
    }
    /**
     * Request permissions.
     */
    public void requestPermission(final Context context,final CallBack callBack,String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(context, R.string.permission_successfully, Toast.LENGTH_SHORT).show();
                        if (callBack!=null)
                        callBack.onSucess();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        Toast.makeText(context, R.string.permission_failure, Toast.LENGTH_SHORT).show();
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            showSettingDialog(context, permissions);
                        }
                    }
                })
                .start();
    }
    /**
     * Request permissions.
     */
    public void requestPermission(final Context context,final CallBack callBack,String[]... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(context, R.string.permission_successfully, Toast.LENGTH_SHORT).show();
                        if (callBack!=null)
                        callBack.onSucess();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        Toast.makeText(context, R.string.permission_failure, Toast.LENGTH_SHORT).show();
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            PermissionUtils.getInstance().showSettingDialog(context, permissions);
                        }
                    }
                })
                .start();
    }
    /**
     * Display setting dialog.
     */
    public void showSettingDialog(final Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.permission_title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.permission_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission(context);
                    }
                })
                .setNegativeButton(R.string.permission_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    /**
     * Set permissions.
     */
    private void setPermission(final Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .start(REQUEST_CODE_SETTING);
    }
    //电池优化白名单
    private AlertDialog alertDialog;
    public void requestPowerPermission(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (powerManager == null) {
            return;
        }
        boolean showAlert = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SHOW_DOZE_ALERT_KEY, true);
        if (!showAlert) {
            return;
        }
        String packageName = context.getPackageName();
        boolean ignoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName);
        if (!ignoringBatteryOptimizations) {
            if (alertDialog==null)
                alertDialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.alert_for_doze_mode_title)
                        .setMessage(R.string.alert_for_doze_mode_content)
                        .setPositiveButton(R.string.alert_for_doze_mode_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    context.startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                            Uri.parse("package:" + context.getPackageName())));
                                } catch (ActivityNotFoundException ignored) {
                                    // ActivityNotFoundException on some devices.
                                    try {
                                        context.startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                                    } catch (Throwable e) {
                                        PreferenceManager.getDefaultSharedPreferences(context)
                                                .edit().putBoolean(SHOW_DOZE_ALERT_KEY, false).apply();
                                    }
                                } catch (Throwable e) {
                                    PreferenceManager.getDefaultSharedPreferences(context)
                                            .edit().putBoolean(SHOW_DOZE_ALERT_KEY, false).apply();
                                }
                            }
                        })
                        .setNegativeButton(R.string.alert_for_doze_mode_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreferenceManager.getDefaultSharedPreferences(context)
                                        .edit().putBoolean(SHOW_DOZE_ALERT_KEY, false).apply();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                alertDialog=null;
                            }
                        })
                        .create();
            try {
                if (alertDialog!=null)
                if (!alertDialog.isShowing())
                    alertDialog.show();
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }
    }
    /**
     * 判断系统弹出权限是否开启
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void requestAlertWindowPermission(Context context) {
        AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context.getPackageName());
        if (mode ==2){
            List<String> permissions=new ArrayList<String>();
            permissions.add("开启悬浮窗");
            showSettingDialog(context,permissions);
        }
    }
    /**
     * 悬浮窗权限
     */
    public void askAlertWindowPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(BaseApplication.getInstance())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" +context.getPackageName()));
                ((AppCompatActivity)context).startActivityForResult(intent, RC_PERMISSION_MANAGE_OVERLAY_PERMISSION);
            }
        }
    }
}
