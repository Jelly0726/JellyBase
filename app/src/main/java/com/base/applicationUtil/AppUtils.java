package com.base.applicationUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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
import android.os.StatFs;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.base.config.BaseConfig;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
     *
     * @param str 需要正则表达式判断的字符串
     * @param regex 正则表达式
     * @param bool 是否区分大小写
     * @return
     */
    public static boolean like(String str,String regex,boolean bool)
    {
        regex = regex.replaceAll("\\*", ".*");
        regex = regex.replaceAll("\\?", ".");
        Pattern pattern = Pattern.compile(regex,bool?Pattern.CASE_INSENSITIVE:0);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();

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
    public static void installApk(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//		intent.setType("application/vnd.android.package-archive");
//		intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 判断字符串是不是手机号码
     * @param paramString
     * @return
     */
    public static boolean isMobileNO(String paramString) {
        return Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-1,5-9]))\\d{8}$").matcher(paramString).matches();
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
     *
     * 获取签名字符串
     * @param map  参数
     * @param type  0 无随机 1随机
     * @return
     */
    public static Map<String, String> getSign(Map<String, String> map, int type){
        if(type==1) {
            String uuid = UUID.randomUUID().toString()
                    .replaceAll("-", "").substring(0, 20).toUpperCase();
            map.put("nonce_str",uuid);
        }
        map=sortMapByKey(map);
        StringBuffer stringBuffer=new StringBuffer("");
        Iterator iterator=map.entrySet().iterator();
        while(iterator.hasNext()){
            LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
            String key= (String) entent.getKey();
            String value= (String) entent.getValue();
            if(!TextUtils.isEmpty(value)) {
                stringBuffer
                        .append(key)
                        .append("=")
                        .append(value);
                if (iterator.hasNext()) {
                    stringBuffer.append("&");
                }
            }
        }
        if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        stringBuffer.append("&key=").append(BaseConfig.KEY);
        //Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
        map.put("sign",MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
        return map;
        //return MD5(stringBuffer.toString()).toUpperCase();
    }
    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }
    //比较器类

    private static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
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
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
//      Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
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
            ActivityManager manager = (ActivityManager)MyApplication.getMyApp().getSystemService(Context.ACTIVITY_SERVICE);
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
     * 取double数据后面两位小数的上界
     */
    public static double getValueWith2Suffix(double dbl){
        BigDecimal bg = new BigDecimal(dbl);
        return bg.setScale(1, BigDecimal.ROUND_CEILING).doubleValue();
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
        // register the Broadcast Receivers
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
            SubscriptionManager sub = (SubscriptionManager) MyApplication.getMyApp()
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
            TelephonyManager tm = (TelephonyManager) MyApplication.getMyApp()
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
        String uuid = AppPrefs.getString(MyApplication.getMyApp(),"UUID","");
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            AppPrefs.putString(MyApplication.getMyApp(),"UUID",uuid);
        }
        return uuid;
    }
    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
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
     整数(秒数)转换为时分秒格式(xx:xx:xx)
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;//时
        int minute = 0;//分
        int second = 0;//秒
        int minse=0;   //毫秒
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 6000;//分
            if (minute <60) {
                if (second <60) {
                    second=(time%6000)/100;   //秒
                    minse = (time%6000) %100;   //毫秒
                    timeStr =unitFormat(minute) +":"+unitFormat(second) +":" + unitFormat(minse);
                }else{

                }
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)+":" + unitFormat(minse);
            }
        }
        return timeStr;
    }
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
    /**
     * 保存HTML文件
     * @param context
     * @param ss
     */
    public static void saveFileHtml(Context context, String ss) {
        try{
            String sdd= Environment.getDataDirectory()+"/files/index.html";
            InputStream in =getStringStream(ss);
            int lenght = in.available();
            //创建byte数组
            byte[]  buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            FileOutputStream outStream = context.openFileOutput(sdd, Context.MODE_PRIVATE);
            outStream.write(buffer);
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 将一个字符串转化为输入流
     */
    public static InputStream getStringStream(String sInputString){
        if (sInputString != null && !sInputString.trim().equals("")){
            try{
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将一个输入流转化为字符串
     */
    public static String getStreamString(InputStream tInputStream){
        if (tInputStream != null){
            try{
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null){
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 字符串非空判断
     * @param string
     * @return
     */
    public static String isNull(String string){
        String ss="";
        if(string!=null){
            if(!string.trim().equals("null")){
                ss=string;
            }
        }

        return ss;
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
    public static String reTimeT(String timeT){
        String time="00-00-00 00:00:00";
        try{
            if(timeT!=null) {
                time = timeT.replace("T", " ");
            }else{
                return time;
            }
        }catch (Exception e){
            return "00-00-00 00:00:00";
        }
        return time;
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
    /**
     * 添加选中下划线
     * @param activity  activity
     * @param button    选中的按钮  Button
     * @param linearLayout  父容器 LinearLayout
     */
    public static void setTabSelected(Activity activity, Button button, LinearLayout linearLayout) {
        Drawable selectedDrawable = ResourceReader.readDrawable(activity,
                CPResourceUtil.getDrawableId(activity,"radiobutton_bottom_line"));
        int screenWidth = AppUtils.getScreenSize(activity)[0];
        int size = linearLayout.getChildCount();
        int right =(int)((screenWidth /size)*0.6);
        //前两个是组件左上角在容器中的坐标 后两个是组件的宽度和高度
        selectedDrawable.setBounds(0, 0,right, AppUtils.dipTopx(activity,1));
        button.setSelected(true);
        button.setCompoundDrawables(null, null, null, selectedDrawable);
        for (int i = 0; i < size; i++) {
            if (button.getId() != linearLayout.getChildAt(i).getId()) {
                linearLayout.getChildAt(i).setSelected(false);
                ((Button) linearLayout.getChildAt(i)).setCompoundDrawables(null, null, null, null);
            }
        }
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
     * 删除指定的文件或目录（无限递归删除，返回是否删除成功）
     * @param FileOrDirectory    文件路径
     * @return
     */
    public static boolean DeleteFileOrDirectory(File FileOrDirectory) {
        if (FileOrDirectory.exists()) {
            if (FileOrDirectory.isFile()) {
                return FileOrDirectory.delete();
            } else if (FileOrDirectory.isDirectory()) {
                File files[] = FileOrDirectory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    DeleteFileOrDirectory(files[i]);
                }
            }
        }
        return false;
    }
    /**
     * 给对象的变量赋值
     * @param set   要赋值的对象 不能为空
     * @param get   要取值的对象 不能为空
     * @return
     */
    public Object setValue(Object set, Object get){
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
                                .equals("interface com.android.tools" +
                                        ".fd.runtime.IncrementalChange"))
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
                                .equals("interface com.android.tools" +
                                        ".fd.runtime.IncrementalChange"))
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
    private String getMethodName(String fieldName) {
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
     * ping
     * @return ip地址是否可用
     */
    public static final boolean ping(String ip) {
        String result = null;
        try {
            //String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);//ping3次
            // 读取ping的内容，可不加。
//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuffer stringBuffer = new StringBuffer();
//            String content = "";
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }
//            Log.i("TTT", "result content : " + stringBuffer.toString());
            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
            }

        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            Log.i("TTT", "result = " + result);
        }
        return false;
    }
    /**
     * 关键字高亮显示
     *
     * @param target  需要高亮的关键字
     * @param text	     需要显示的文字
     * @param color      颜色
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder highlight(String text, String target,int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(MyApplication.getMyApp().getResources().getColor(color));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
    /**
     * 根据值, 设置spinner默认选中:
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value, String modle){
        BaseAdapter apsAdapter= (BaseAdapter) spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        try {
            for (int i = 0; i < k; i++) {
                Object obj = apsAdapter.getItem(i);
                Class cla = obj.getClass();
                Field f = cla.getDeclaredField(modle);// 属性的值
                f.setAccessible(true);    // Very Important
                Object valu = (Object) f.get(obj);
                //Log.i("msg","value="+value+" valu="+valu);
                if (value.trim().equals(valu.toString().trim())) {
                    spinner.setSelection(i,true);// 默认选中项
                    break;
                }
            }
        }catch (Exception e){
            //Log.i("msg","e="+e.toString());
        }
    }
}
