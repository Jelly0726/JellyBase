package com.base.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.base.applicationUtil.AppUtils;
import com.base.applicationUtil.CameraProvider;
import com.jelly.jellybase.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

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
    public static final int REQ_CODE=520;
    private static final String SHOW_DOZE_ALERT_KEY = "SHOW_DOZE_ALERT_KEY";

    private PermissionUtils() {
    }

    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder {
        private static final PermissionUtils instance = new PermissionUtils();
    }

    /**
     * 单一实例
     */
    public static PermissionUtils getInstance() {
        return PermissionUtils.SingletonHolder.instance;
    }

    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     *
     * @return
     * @throws ObjectStreamException
     */
    public Object readResolve() throws ObjectStreamException {
        return PermissionUtils.SingletonHolder.instance;
    }

    /**
     * Request permissions.
     */
    public void requestPermission(final Context context, final CallBack callBack, String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (callBack != null)
                            callBack.onSucess();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        List<String> perm = new ArrayList<>();
                        perm.addAll(permissions);
                        for (int i = 0; i < perm.size(); i++) {
                            if (perm.get(i).contains("CAMERA")) {
                                if (!CameraProvider.isCameraCanUse())
                                    permissions.remove(perm.get(i));
                            } else if (perm.get(i).contains("STORAGE")) {
                                if (!AppUtils.isSDcardExist())
                                    permissions.remove(perm.get(i));
                            } else if ((perm.get(i).contains("SYSTEM_ALERT_WINDOW"))) {
                                permissions.remove(perm.get(i));
                            }
                        }
                        if (permissions.size() <= 0) {
                            if (callBack != null)
                                callBack.onSucess();
                            return;
                        }
                        Toast.makeText(context, R.string.permission_failure, Toast.LENGTH_SHORT).show();
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            showSettingDialog(context, permissions, callBack);
                        }else {
                            if (callBack != null)
                                callBack.onFailure(permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * Request permissions.
     */
    public void requestPermission(final Context context, final CallBack callBack, String[]... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (callBack != null)
                            callBack.onSucess();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        List<String> perm = new ArrayList<>();
                        perm.addAll(permissions);
                        for (int i = 0; i < perm.size(); i++) {
                            if (perm.get(i).contains("CAMERA")) {
                                if (!CameraProvider.isCameraCanUse())
                                    permissions.remove(perm.get(i));
                            } else if (perm.get(i).contains("STORAGE")) {
                                if (!AppUtils.isSDcardExist())
                                    permissions.remove(perm.get(i));
                            } else if ((perm.get(i).contains("SYSTEM_ALERT_WINDOW"))) {
                                permissions.remove(perm.get(i));
                            }
                        }
                        if (permissions.size() <= 0) {
                            if (callBack != null)
                                callBack.onSucess();
                            return;
                        }
                        Toast.makeText(context, R.string.permission_failure, Toast.LENGTH_SHORT).show();
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            showSettingDialog(context, permissions, callBack);
                        }else {
                            if (callBack != null)
                                callBack.onFailure(permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(final Context context, final List<String> permissions, final CallBack callBack) {
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
                        permissions.clear();
                        if (callBack != null)
                            callBack.onFailure(permissions);
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
                .start(0);
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
            String message = context.getString(R.string.alert_for_doze_mode_content, context.getString(R.string.app_name));
            if (alertDialog == null)
                alertDialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.alert_for_doze_mode_title)
                        .setMessage(message)
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
                                alertDialog = null;
                            }
                        })
                        .create();
            try {
                if (alertDialog != null)
                    if (!alertDialog.isShowing())
                        alertDialog.show();
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }
    }
    /**
     * 申请目录的访问权限
     * 会打开系统的文件目录，由用户自己选择允许访问的目录，不用申请WRITE/READ_EXTERNAL_STORAGE权限。
     * 允许之后通过onActivityResult()的intent.getData()得到该目录的Uri，通过Uri可获取子目录和文件。
     * 这种方式的缺点是应用重装后权限失效，即使保存这个Uri也没用。
     *
     * Uri dirUri = intent.getData();
     * // 持久化；应用重装后权限失效，即使知道这个uri也没用
     * SPUtil.setValue(this, SP_DOC_KEY, dirUri.toString());
     * //重要：少这行代码手机重启后会失去权限
     * getContentResolver().takePersistableUriPermission(dirUri,
     *         Intent.FLAG_GRANT_READ_URI_PERMISSION);
     * @param activity
     */
    public void openFilePermission(Activity activity){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        activity.startActivityForResult(intent, REQ_CODE);
    }
}
