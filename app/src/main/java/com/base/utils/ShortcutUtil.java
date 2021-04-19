package com.base.utils;

import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;

import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.jellybase.R;
import com.jelly.jellybase.receiver.PhoneStatReceiver;

/**
 * 桌面快捷方式管理类
 */
public class ShortcutUtil {
    //检测是否存在快捷方式
    private static boolean hasShortcut(Context context)
    {
        boolean isInstallShortcut = false;
        try{
            final ContentResolver cr = context.getApplicationContext().getContentResolver();
            final Uri CONTENT_URI = Uri.parse("content://com.android.launcher2.settings/favorites?notify=true");//保持默认
            Cursor c = cr.query(CONTENT_URI,new String[] {"title","iconResource" },"title=?", //保持默认
                    //getString(R.string.app_name)是获取string配置文件中的程序名字，这里用一个String的字符串也可以
                    new String[] {context.getApplicationContext().getString(R.string.app_name).trim()}, null);
            if(c!=null && c.getCount()>0){
                isInstallShortcut = true ;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isInstallShortcut ;
    }
    /**
     * 创建桌面快捷方式
     * 8.0以下手机提示应用未安装
     * 在AndroidManifest文件里快捷方式启动的页面acitivity标签里添加android:exported="true"
     * 8.0系统点击创建快捷方式无反应
     * 将应用权限列表中的 创建桌面快捷方式 权限打开
     */
    public static void createShut(Context context) {
        if (hasShortcut(context)){return;}
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                //获取shortcutManager
                ShortcutManager shortcutManager = (ShortcutManager) context.getApplicationContext()
                        .getSystemService(Context.SHORTCUT_SERVICE);
                //如果默认桌面支持requestPinShortcut（ShortcutInfo，IntentSender）方法，则返回TRUE。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0之后版本
                    if(shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()){
                        Intent shortCutIntent = new Intent(context.getApplicationContext(), LauncherActivity.class);//快捷方式启动页面
                        shortCutIntent.setAction(Intent.ACTION_VIEW);
                        //快捷方式创建相关信息。图标名字 id
                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context.getApplicationContext(),"shortcutid")
                                .setIcon(Icon.createWithResource(context.getApplicationContext(),R.drawable.ic_launcher))
                                .setShortLabel(context.getApplicationContext().getResources().getString(R.string.app_name))
                                .setIntent(shortCutIntent)
                                .build();
                        //创建快捷方式时候回调
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),0,new
                                Intent(context.getApplicationContext(), PhoneStatReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
                        shortcutManager.requestPinShortcut(shortcutInfo,pendingIntent.getIntentSender());
                        AppPrefs.putBoolean(context.getApplicationContext(), ConfigKey.FIRST,false);
                        return;
                    }
                }

            }
            //8.0以前版本
            // 创建添加快捷方式的Intent
            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            String title = context.getApplicationContext().getResources().getString(R.string.app_name);
            // 加载快捷方式的图标
            Parcelable icon = Intent.ShortcutIconResource.fromContext(
                    context.getApplicationContext(), R.mipmap.ic_launcher);
            // 创建点击快捷方式后操作Intent,该处当点击创建的快捷方式后，再次启动该程序
            Intent myIntent = new Intent(context.getApplicationContext(),
                    LauncherActivity.class);
            myIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载
            // 设置快捷方式的标题
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
            // 设置快捷方式的图标
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            // 设置快捷方式对应的Intent
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
            addIntent.putExtra("duplicate", false); //不允许重复创建
            // 发送广播添加快捷方式
            context.getApplicationContext().sendBroadcast(addIntent);
            AppPrefs.putBoolean(context.getApplicationContext(), ConfigKey.FIRST,false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //删除快捷方式
    public static void delShortcut(Context context){
        try{
            Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
            //快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getApplicationContext().getString(R.string.app_name));
            //这里的intent要和创建时的intent设置一致
            Intent intent = new Intent(context.getApplicationContext(),LauncherActivity.class);
            intent.setAction("com.hooypay.Activity.logo");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            context.getApplicationContext().sendBroadcast(shortcut);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
