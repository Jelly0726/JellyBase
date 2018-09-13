package com.base.permission;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.jelly.jellybase.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.io.ObjectStreamException;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 */

public class PermissionUtils {
    //设置请求码
    public static final int REQUEST_CODE_SETTING = 300;
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
    public void requestPermission(final Context context,String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(context, R.string.permission_successfully, Toast.LENGTH_SHORT).show();
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
    public void requestPermission(final Context context,String[]... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(context, R.string.permission_successfully, Toast.LENGTH_SHORT).show();
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
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        Toast.makeText(context, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

}
