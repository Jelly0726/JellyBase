package com.base.applicationUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Icon;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.base.LauncherActivity;
import com.base.appManager.BaseApplication;
import com.base.config.ConfigKey;
import com.base.encrypt.MD5;
import com.jelly.jellybase.R;
import com.jelly.jellybase.receiver.PhoneStatReceiver;

import org.apache.commons.beanutils.ConvertUtilsBean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    //保证该类不能被实例化
    private AppUtils() {

    }

    /**
     * 判断是否有存储卡，有返回TRUE，否则FALSE
     *
     * @return
     */
    public static boolean isSDcardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//
            return true;
        } else {
            return false;
        }
    }

    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //Formatter.formatFileSize(this, (freeBlocks * blockSize));		//系统自动装换的格式
        //return freeBlocks * blockSize;  									//单位Byte
        //return (freeBlocks * blockSize)/1024;   							//单位KB
        return (freeBlocks * blockSize) / 1024 / 1024;                        //单位MB
    }

    /**
     * 检查某个应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean checkAPP(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     *  获取当前应用程序的版本
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        // 获取手机的包管理者
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            // 不可能发生.
            // can't reach
            return "0";
        }
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String Phone_VERSION = Build.VERSION.RELEASE;//获取版本号
    public static String Phone_MODEL = Build.MODEL; //获取手机型号

    /**
     * 安装一个新的apk
     * @param file
     */
    public static void installApk(Context context,File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取本机号码
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }
    /**
     * 判断activity是否在栈顶运行
     * @param context
     * @param activity
     * @return
     */
    public static boolean isStar(Context context, Class activity){
        boolean bIsExist=false;
        ActivityManager manager = (ActivityManager)context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskInfoList = manager.getRunningTasks(10);
        for (RunningTaskInfo taskInfo : taskInfoList) {
            // Log.i("wf","baseActivity="+taskInfo.topActivity.getClassName());
            if (taskInfo.topActivity.getClassName().equals(activity.getName())) { // 说明它已经启动了
//                String name = taskInfo.topActivity.getClassName();
//                Log.i("wf","baseActivity="+taskInfo.topActivity.getClassName()+" name="+name);
//                if(name.equals(activity.getName())) {
                bIsExist = true;
                //}
                break;
            }
        }
        return bIsExist;
//    if (bIsExist) {
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("isExit", (Boolean) true); //让它自行关闭
//        this.startActivity(intent);
//    }
    }
    /**
     * 将文本内容放到系统剪贴板里
     * @param str
     * @param context
     */
    public static void onClickCopy(String str, Context context) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(str);
        Toast.makeText(context, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
    }
    /**
     * 打电话
     * @param context
     * @param phone_num
     */
    public static void callPhone(Context context, String phone_num) {

        TelephonyManager manager = (TelephonyManager) context.
                getSystemService(Context.TELEPHONY_SERVICE);
        switch (manager.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                Intent phoneIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + phone_num));
                PackageManager pm = context.getPackageManager();
                if (pm.checkPermission(Manifest.permission.CALL_PHONE,
                        context.getPackageName())
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(phoneIntent);
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                Toast.makeText(context, "无SIM卡", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "SIM卡被锁定或未知状态", Toast.LENGTH_LONG).show();
                break;
        }
    }
    /**
     * 判断应用否是处于运行状态.
     * @param context
     * @return
     */
    public static boolean isAppRunning(Context context , String myPackageName){
        if (null == myPackageName) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> infos = am.getRunningTasks(100);
//		String myPackageName = "com.agchauffeur";
        for(RunningTaskInfo info : infos){
            if (myPackageName.equals(info.topActivity.getPackageName()) || myPackageName.equals(info.baseActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断服务否是处于运行状态.
     * @param context
     * @return
     */
    public static boolean isServiceRunning(Context context , String myPackageName){
        if (null == myPackageName) {
            return false;
        }
        try{
            //检查Service状态
            ActivityManager manager = (ActivityManager) BaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
                if(myPackageName.equals(service.service.getClassName()))

                {
                    return true;
                }
            }
        }catch(Exception e){
            //e.printStackTrace();
            return false;
        }
        return false;
    }
    /**
     * 发送短信
     *
     * @param mobile
     * @param content
     * @param context
     */
    public static void sendMessage(String mobile, String content, Context context) {
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SENT_SMS_ACTION"), 0);
        if (content.length() >= 70) { // 短信字数大于70，自动分条
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(mobile, null, str, sentIntent, null); // 短信发送
            }
        } else {
            smsManager.sendTextMessage(mobile, null, content, sentIntent, null);
        }
        // attach the Broadcast Receivers
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }
        }, new IntentFilter("SENT_SMS_ACTION"));
    }
    /**
     * 获取屏幕的宽
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
    /**
     * 获取屏幕的高
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
    /**
     * 获取屏幕的密度
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context, int i) {
        int dp=0;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        dp=(int) (i*dm.density);
        return dp;
    }
    /**
     * 获取屏幕的宽和高
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] screenSize = new int[2];
        int measuredWidth = 0;
        int measuredheight = 0;
        Point size = new Point();
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredheight = size.y;
        } else {
            android.view.Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredheight = d.getHeight();
        }
        screenSize[0] = measuredWidth;
        screenSize[1] = measuredheight;

        return screenSize;
    }
    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
    /**
     * 根据手机分辨率从DP转成PX
     * @param context
     * @param dpValue
     * @return
     */
    public static int dipTopx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    public static int spTopx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率PX(像素)转成DP
     * @param context
     * @param pxValue
     * @return
     */
    public static int pxTodip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue
     * @return
     */

    public static int pxTosp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    /**
     * 获取手机唯一标识（MD5加密）
     */
    public static String getuniqueId(android.app.Application application) {
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (TextUtils.isEmpty(imei)){
                imei= getUUID();
            }
            //序列号（SerialNumber ）
            String SerialNumber  = getSimIccId();
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID();
            UUID deviceId = new UUID(uuid.hashCode(), ((long)imei.hashCode() << 32) | SerialNumber .hashCode());
            return deviceId.toString();
        } catch (Exception e) {
            Log.i("ssss","e="+e);
            return getUUID();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private static String getSimIccId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //大于等于Android 5.1.0 L版本
            SubscriptionManager sub = (SubscriptionManager) BaseApplication.getInstance()
                    .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> info = sub.getActiveSubscriptionInfoList();
            int count = sub.getActiveSubscriptionInfoCount();
            if (count > 0) {
                if (count > 1) {
                    String icc1 = info.get(0).getIccId();
                    String icc2 = info.get(1).getIccId();
                    return icc1 + "," + icc2;
                } else {
                    for (SubscriptionInfo list : info) {
                        String icc1 = list.getIccId();
                        return icc1;
                    }
                }
            } else {
                Log.d("PhoneUtil", "无SIM卡");
                return getUUID();
            }
        } else { //小于5.1.0 以下的版本
            TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String SerialNumber  =tm.getSimSerialNumber();
            if (TextUtils.isEmpty(SerialNumber)){
                SerialNumber=getUUID();
            }
            return SerialNumber;
        }
        return getUUID();
    }
    private static String getUUID(){
        //如果上面都没有， 则生成一个id：随机码
        String uuid = AppPrefs.getString(BaseApplication.getInstance(),"UUID","");
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            AppPrefs.putString(BaseApplication.getInstance(),"UUID",uuid);
        }
        return uuid;
    }
    //文件存储根目录
    public static String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }



    /**
     * 异步获取外网ip
     * @param  type I ip格式 Z 中文格式 H 组合格式
     * @param  getIPAsyncTaskBack 异步获取IP回调接口 在UI线程执行
     * @return
     */
    private static GetIPAsyncTask getIPAsyncTask;
    public static void getNetIp(String type, GetIPAsyncTaskBack getIPAsyncTaskBack){
        if(getIPAsyncTask!=null){
            getIPAsyncTask.cancel(true);
        }
        getIPAsyncTask=null;
        getIPAsyncTask=new GetIPAsyncTask(getIPAsyncTaskBack);
        getIPAsyncTask.execute(type);
    }

    /**
     * 构造函数AsyncTask<Params, Progress, Result>参数说明:
     Params   启动任务执行的输入参数
     Progress 后台任务执行的进度
     Result   后台计算结果的类型
     */
    private static class GetIPAsyncTask extends AsyncTask<String,Integer,String> {
        private GetIPAsyncTaskBack getIPAsyncTaskBack;
        public GetIPAsyncTask(GetIPAsyncTaskBack getIPAsyncTaskBack){
            this.getIPAsyncTaskBack=getIPAsyncTaskBack;
        }
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected String doInBackground(final String... params) {
            String IP = "127.0.0.1";
            try {
                final ExecutorService exec= Executors.newFixedThreadPool(3);
                Callable<String> call=new Callable<String>(){
                    public String call() throws Exception {
                        String ip = "127.0.0.1";
                        String chinaz = "http://ip.chinaz.com";

                        StringBuilder inputLine = new StringBuilder();
                        String read = "";
                        URL url = null;
                        HttpURLConnection urlConnection = null;
                        BufferedReader in = null;
                        try {
                            url = new URL(chinaz);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                            while((read=in.readLine())!=null){
                                inputLine.append(read+"\r\n");
                            }
                            //System.out.println(inputLine.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally{
                            if(in!=null){
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }


                        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
                        Matcher m = p.matcher(inputLine.toString());
                        if(m.find()){
                            String ipstr = m.group(1);
                            ip = ipstr;
                            //System.out.println(ipstr);
                        }
                        return ip;
                    }
                };
                // 开始任务执行服务
                Future<String> task=exec.submit(call);
                IP=task.get();
                // 停止任务执行服务
                exec.shutdown();
                return IP;
            }catch (Exception e){
                IP = "127.0.0.1";
                Log.e("提示", "网络连接异常，无法获取IP地址！e="+e.toString());
            }
            return IP;
        }
        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行 在主线程中执行的操作
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(getIPAsyncTaskBack!=null){
                getIPAsyncTaskBack.GetIP(s);
            }
        }
        /**
         * 方法用于异步任务被取消时,在主线程中执行相关的操作
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        /**
         * 方法用于异步任务被取消时,在主线程中执行相关的操作
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    /**
     * 异步获取Ip回调接口
     */
    public interface GetIPAsyncTaskBack{
        public void GetIP(String IP);
    }
    /**
     * 获取IP
     * @param context
     * @return
     */
    public static String getIP(Context context){
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                //wifiManager.setWifiEnabled(true);
                return getLocalIpAddress();
            }else {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                return intToIp(ipAddress);
            }
        }catch (Exception e){
            return "127.0.0.1";
        }
    }
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    private static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            return "127.0.0.1";
            //Log.e("WifiPreference IpAddress", ex.toString());
        }
        return "127.0.0.1";
    }
    /** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /** * 按名字清除本应用数据库 * * @param context * @param dbName */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /** * 清除本应用所有的数据 * * @param context * @param filepath */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
    private static TelephonyManager sTelManager;

    /**
     *  判断手机是否有GPS
     * @param paramContext
     * @return
     */
    public static boolean hasGps(Context paramContext)
    {
        LocationManager localLocationManager = (LocationManager)paramContext.getSystemService(Context.LOCATION_SERVICE);
        if (localLocationManager != null)
        {
            LocationProvider localLocationProvider = localLocationManager.getProvider("gps");
            if (localLocationProvider != null)
                return true;
            else return false;
        } else return false;
    }
    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        try{
            LocationManager locationManager
                    = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if(locationManager!=null){
                // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
                boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
                boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                //|| network
                if (gps) {
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        try{
            Intent GPSIntent = new Intent();
            GPSIntent.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
            GPSIntent.setData(Uri.parse("custom:3"));
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 判断移动网络是否启用
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        try{
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if(mConnectivityManager!=null){
                    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (mNetworkInfo != null) {
                        return mNetworkInfo.isAvailable();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 判断网络是否启用（WLAN、移动网络）
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try{
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected())
                {
                    // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        // 当前所连接的网络可用
                        return true;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 判断WIFI是否可用
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        try{
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /***
     * 打开系统设置
     * @param activity
     * @param settings
     */
    public static void openSetting(Activity activity, String settings){
       /* Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                settings);
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");*/
        Intent intent = new Intent(settings);
        activity.startActivityForResult(intent , 0);
    }

    /**
     * 给对象的变量赋值
     * @param set   要赋值的对象 不能为空
     * @param get   要取值的对象 不能为空
     * @return
     */
    public static Object setValue(Object set, Object get){
        if(set!=null&&get!=null) {
            Class setTemp = set.getClass(); // 获取Class类的对象的方法之一
            Class getTemp = get.getClass(); // 获取Class类的对象的方法之一
            Field[] setfb = setTemp.getDeclaredFields();//返回所有属性的成员变量的数组
            Field[] getfb = getTemp.getDeclaredFields();//返回所有属性的成员变量的数组
            //List setList=Arrays.asList(setfb);//数组转list
            List getList= Arrays.asList(getfb);//数组转list
            try {
                for (int i = 0; i < setfb.length; i++) {
                    if(getList.contains(setfb[i])) {
                        if(setfb[i].getName().equals("serialVersionUID")){
                            continue;
                        }
                        Class cl = setfb[i].getType();    // 属性的类型
                        if(cl.toString()
                                .equals("interface com.android.tools.fd.runtime.IncrementalChange")
                                ||cl.toString()
                                .equals("interface com.android.tools.ir.runtime.IncrementalChange"))
                            continue;
                        int md = setfb[i].getModifiers();    // 属性的修饰域
                        Field f = getTemp.getDeclaredField(setfb[i].getName());// 属性的值
                        f.setAccessible(true);    // Very Important
                        Object value = (Object) f.get(get);
                        if(value==null) {
                            value=new ConvertUtilsBean().convert(0,cl);
                        }
                        Method method = set.getClass()
                                .getMethod("set" + getMethodName(setfb[i].getName()), cl);
                        method.invoke(set, value);
                        //Object returnObj = ConvertUtils.convert(str, clazz);
                    }else{
                        if(setfb[i].getName().equals("serialVersionUID")){
                            continue;
                        }
                        Class cl = setfb[i].getType();    // 属性的类型
                        if(cl.toString()
                                .equals("interface com.android.tools.fd.runtime.IncrementalChange")
                                ||cl.toString()
                                .equals("interface com.android.tools.ir.runtime.IncrementalChange"))
                            continue;
                        int md = setfb[i].getModifiers();    // 属性的修饰域
                        Field f = setTemp.getDeclaredField(setfb[i].getName());// 属性的值
                        f.setAccessible(true);    // Very Important
                        Object value =  f.get(set);
                        if(value==null) {
                            value = new ConvertUtilsBean().convert(0, cl);
                            Method method = set.getClass()
                                    .getMethod("set" + getMethodName(setfb[i].getName()), cl);
                            method.invoke(set, value);
                        }
                    }
                }

                return set;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    /**
     * 将属性名称的首字母变成大写
     * */
    private static String getMethodName(String fieldName) {
        byte[] bytes = fieldName.getBytes();
        bytes[0] = (byte) (bytes[0] - 'a' + 'A');
        return new String(bytes);
    }
    /**
     * 打开其他app 有安装就打开 没安装就去下载
     * @param context    上下文
     * @param packageName  包名
     * @param url          下载的网页地址
     */
    public static void openApp(Context context, String packageName, String url){
        try {
            PackageManager packageManager =context.getPackageManager();
            Intent intent=new Intent();
            intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent viewIntent = new
                    Intent("android.intent.action.VIEW", Uri.parse(url));
            context.startActivity(viewIntent);
        }
    }

    /**
     * 获取本应用的签名信息
     * @param context
     * @return
     */
    public static String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                // MediaApplication.logD(DownloadApk.class, packageinfo.signatures[0].toCharsString());
                Map map=parseSignature(packageinfo.signatures[0].toByteArray());
                String ss="SigAlgName：" + map.get("SigAlgName")
                        +"\npubKey：" + map.get("pubKey")+
                        "\nsignNumber：" + map.get("signNumber")
                        +"\nsubjectDN：" +map.get("subjectDN")
                        +"\n姓名："+map.get("name")
                        +"\nIssuerDN："+map.get("IssuerDN")
                        +"\n有效期开始："+map.get("NotBefore")
                        +"\n有效期限："+map.get("NotAfter")
                        +"\nTBSCertificate："+map.get("TBSCertificate")
                        +"\nSignature："+map.get("Signature")
                        +"\nSigAlgOID："+map.get("SigAlgOID")
                        +"\nSigAlgParams："+map.get("SigAlgParams")
                        ;
                return  ss;
                //return packageinfo.signatures[0].toCharsString();
            }
        }
        return null;
    }
    /**
     * 根据包名获取的签名信息
     * @param context
     * @return
     */
    public static String getSign(Context context,String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            if (packageName.equals(context.getPackageName())) {
                // MediaApplication.logD(DownloadApk.class, packageinfo.signatures[0].toCharsString());
                Map map=parseSignature(packageinfo.signatures[0].toByteArray());
                String ss="SigAlgName：" + map.get("SigAlgName")
                        +"\npubKey：" + map.get("pubKey")+
                        "\nsignNumber：" + map.get("signNumber")
                        +"\nsubjectDN：" +map.get("subjectDN")
                        +"\n姓名："+map.get("name")
                        +"\nIssuerDN："+map.get("IssuerDN")
                        +"\n有效期开始："+map.get("NotBefore")
                        +"\n有效期限："+map.get("NotAfter")
                        +"\nTBSCertificate："+map.get("TBSCertificate")
                        +"\nSignature："+map.get("Signature")
                        +"\nSigAlgOID："+map.get("SigAlgOID")
                        +"\nSigAlgParams："+map.get("SigAlgParams")
                        ;
                return  ss;
                //return packageinfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    /**
     * 获取PAK的签名信息
     * @param context
     */
    public static Map getSingInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 根据包名获取PAK的签名信息
     * @param context
     * @param packageName
     */
    public static Map getSingInfo(Context context,String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取APk签名信息
     * @return
     */
    private static Map parseSignature(byte[] signature) {
        Map map=new TreeMap();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            String[] str = cert.getSubjectDN().toString().split(",");
            int num=str[0].indexOf("=");
            String locationx = str[0].substring(num+1, str[0].length());
            map.put("name", locationx);
            map.put("SigAlgName",  cert.getSigAlgName());
            map.put("pubKey", pubKey);
            map.put("signNumber", signNumber);
            map.put("subjectDN", cert.getSubjectDN());
            map.put("IssuerDN", cert.getIssuerDN());
            map.put("NotBefore", cert.getNotBefore());
            map.put("NotAfter", cert.getNotAfter());
            map.put("TBSCertificate", new String(cert.getTBSCertificate()));
            map.put("Signature", new String(cert.getSignature()));
            map.put("SigAlgOID", cert.getSigAlgOID());
            map.put("SigAlgParams", new String(cert.getSigAlgParams()));
            return map;
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert
     * *file D:\Desktop\CERT.RSA’获取的md5值一样
     */
    public static String getSignMd5Str(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = MD5.MD5Encode(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert
     * *file D:\Desktop\CERT.RSA’获取的md5值一样
     */
    public static String getSignMd5Str(Context context,String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = MD5.MD5Encode(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取未安装Apk的签名
     *
     * @param apkPath
     * @return
     */
    public static String getApkSignature(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            // apk包的文件路径
            // 这是一个Package 解释器, 是隐藏的
            // 构造函数的参数只有一个, apk文件的路径
            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser;
            if (Build.VERSION.SDK_INT > 19) {
                pkgParser = pkgParserCls.newInstance();
            } else {
                Constructor constructor = pkgParserCls.getConstructor(typeArgs);
                pkgParser = constructor.newInstance(valueArgs);
            }

            // 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            // PackageParser.Package mPkgInfo = packageParser.parsePackage(new
            // File(apkPath), apkPath,
            // metrics, 0);
            Object pkgParserPkg = null;
            if (Build.VERSION.SDK_INT > 19) {
                valueArgs = new Object[2];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = PackageManager.GET_SIGNATURES;
                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                        "parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                typeArgs = new Class[2];
                typeArgs[0] = File.class;
                typeArgs[1] = int.class;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        valueArgs);
            } else {
                typeArgs = new Class[4];
                typeArgs[0] = File.class;
                typeArgs[1] = String.class;
                typeArgs[2] = DisplayMetrics.class;
                typeArgs[3] = int.class;

                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                        "parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[4];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = apkPath;
                valueArgs[2] = metrics;
                valueArgs[3] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        valueArgs);
            }


            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = int.class;
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info[0].toCharsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * 使用反射解决
     * 在 onSaveInstanceState 调用之后调用了 popBackStackImmediate 。导致崩溃问题
     */
    public static void fixBug(FragmentManager fragmentManager) {
        try {
            Class<? extends FragmentManager> aClass =fragmentManager.getClass();
            Method method = aClass.getMethod("noteStateNotSaved");
            method.setAccessible(true);
            method.invoke(fragmentManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //检测是否存在快捷方式
    private static boolean hasShortcut()
    {
        boolean isInstallShortcut = false;
        try{
            final ContentResolver cr = BaseApplication.getInstance().getContentResolver();
            final Uri CONTENT_URI = Uri.parse("content://com.android.launcher2.settings/favorites?notify=true");//保持默认
            Cursor c = cr.query(CONTENT_URI,new String[] {"title","iconResource" },"title=?", //保持默认
                    //getString(R.string.app_name)是获取string配置文件中的程序名字，这里用一个String的字符串也可以
                    new String[] {BaseApplication.getInstance().getString(R.string.app_name).trim()}, null);
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
    public static void createShut() {
        if (hasShortcut()){return;}
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                //获取shortcutManager
                ShortcutManager shortcutManager = (ShortcutManager) BaseApplication.getInstance()
                        .getSystemService(Context.SHORTCUT_SERVICE);
                //如果默认桌面支持requestPinShortcut（ShortcutInfo，IntentSender）方法，则返回TRUE。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0之后版本
                    if(shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()){
                        Intent shortCutIntent = new Intent(BaseApplication.getInstance(),LauncherActivity.class);//快捷方式启动页面
                        shortCutIntent.setAction(Intent.ACTION_VIEW);
                        //快捷方式创建相关信息。图标名字 id
                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(BaseApplication.getInstance(),"shortcutid")
                                .setIcon(Icon.createWithResource(BaseApplication.getInstance(),R.drawable.ic_launcher))
                                .setShortLabel(BaseApplication.getInstance().getResources().getString(R.string.app_name))
                                .setIntent(shortCutIntent)
                                .build();
                        //创建快捷方式时候回调
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseApplication.getInstance(),0,new
                                Intent(BaseApplication.getInstance(), PhoneStatReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
                        shortcutManager.requestPinShortcut(shortcutInfo,pendingIntent.getIntentSender());
                        AppPrefs.putBoolean(BaseApplication.getInstance(), ConfigKey.FIRST,false);
                        return;
                    }
                }

            }
            //8.0以前版本
            // 创建添加快捷方式的Intent
            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            String title = BaseApplication.getInstance().getResources().getString(R.string.app_name);
            // 加载快捷方式的图标
            Parcelable icon = Intent.ShortcutIconResource.fromContext(
                    BaseApplication.getInstance(), R.mipmap.ic_launcher);
            // 创建点击快捷方式后操作Intent,该处当点击创建的快捷方式后，再次启动该程序
            Intent myIntent = new Intent(BaseApplication.getInstance(),
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
            BaseApplication.getInstance().sendBroadcast(addIntent);
            AppPrefs.putBoolean(BaseApplication.getInstance(), ConfigKey.FIRST,false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //删除快捷方式
    public static void delShortcut(){
        try{
            Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
            //快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, BaseApplication.getInstance().getString(R.string.app_name));
            //这里的intent要和创建时的intent设置一致
            Intent intent = new Intent(BaseApplication.getInstance(),LauncherActivity.class);
            intent.setAction("com.hooypay.Activity.logo");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            BaseApplication.getInstance().sendBroadcast(shortcut);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
