package com.base.applicationUtil;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.base.Utils.StringUtil;
import com.base.appManager.BaseApplication;
import com.base.log.DebugLog;
import com.base.toast.ToastUtils;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.getSerial;

public class PhoneUtil {
    private static TelephonyManager sTelManager;

    public static boolean hasGps(Context paramContext) {
        LocationManager localLocationManager = (LocationManager) paramContext.getSystemService(Context.LOCATION_SERVICE);
        if (localLocationManager != null) {
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
        try {
            LocationManager locationManager
                    = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
                boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
                boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                //|| network
                if (gps) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        try {
            Intent GPSIntent = new Intent();
            GPSIntent.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
            GPSIntent.setData(Uri.parse("custom:3"));
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断移动网络是否启用
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (mConnectivityManager != null) {
                    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (mNetworkInfo != null) {
                        return mNetworkInfo.isAvailable();
                    }
                }
            }
        } catch (Exception e) {
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
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        // 当前所连接的网络可用
                        return true;
                    }
                }
            }
        } catch (Exception e) {
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
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取安卓手机的唯一标识
     * @param context
     * @return
     */
    public static String getUniqueID(Context context) {
        String UniqueID="";
//        String AndroidId=getAndroidId(context);
//        if (!StringUtil.isEmpty(AndroidId)){
//            UniqueID=UniqueID+AndroidId;
//        }
        String DeviceId=getDeviceId(context);
        if (!StringUtil.isEmpty(DeviceId)){
            UniqueID=UniqueID+DeviceId;
        }
        String BTMAC=getBTMAC(context);
        if (!StringUtil.isEmpty(BTMAC)){
            UniqueID=UniqueID+BTMAC;
        }
        String SimSerialNumber=getSimSerialNumber(context);
        if (!StringUtil.isEmpty(SimSerialNumber)){
            UniqueID=UniqueID+SimSerialNumber;
        }
        String SerialNumber=getSerialNumber(context);
        if (!StringUtil.isEmpty(SerialNumber)){
            UniqueID=UniqueID+SerialNumber;
        }
        String PseudoUniqueID=getPseudoUniqueID();
        if (!StringUtil.isEmpty(PseudoUniqueID)){
            UniqueID=UniqueID+PseudoUniqueID;
        }
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(UniqueID.getBytes(),0,UniqueID.length());
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte p_md5Data[] = m.digest();
        String m_szUniqueID = new String();
        for (int i=0;i<p_md5Data.length;i++) {
            //循环每个字符 将计算结果转化为正整数;
            int b = (0xFF & p_md5Data[i]);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (b <= 0xF) {
                m_szUniqueID+="0";
            }
            //将10进制转化为较短的16进制
            m_szUniqueID+=Integer.toHexString(b);
        }
        //将结果全部小写
        m_szUniqueID = m_szUniqueID.toLowerCase();
        return "AID"+m_szUniqueID;

    }
    /**
     * 获取Android id
     * 在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来。不需要权限，平板设备通用。
     * 获取成功率也较高，缺点是设备恢复出厂设置会重置。另外就是某些厂商的低版本系统会有bug，返回的都是相同的AndroidId
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
      获取安卓手机的DeviceId（imei）
      需要android.permission.READ_PHONE_STATE权限，它在6.0+系统中是需要动态申请的。如果需求要求App启动时上报设备
      标识符的话，那么第一会影响初始化速度，第二还有可能被用户拒绝授权。
      android系统碎片化严重，有的手机可能拿不到DeviceId，会返回null或者000000。
      这个方法是只对有电话功能的设备有效的，在pad上不起作用。 可以看下方法注释
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ToastUtils.showShort(context, "缺少{Manifest.permission.READ_PHONE_STATE}权限");
            return "";
        }
        return tm.getDeviceId();

    }

    /**
     *  获取Pseudo-Unique ID
     * @return
     */
    public static String getPseudoUniqueID(){
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
        return m_szDevIDShort;
    }

    /**
     * 获取蓝牙MAC地址
     * Mac地址 – 属于Android系统的保护信息，高版本系统获取的话容易失败，返回0000000；
     * @return
     */
    public static String getBTMAC(Context context){
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ToastUtils.showShort(context, "缺少{Manifest.permission.BLUETOOTH }权限");
            return "";
        }
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();
        return m_szBTMAC;
    }
    /**
     * 获取 sim编号
     * SimSerialNum – 显而易见，只能用在插了Sim的设备上，通用性不好。而且需要android.permission.READ_PHONE_STATE权限
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ToastUtils.showShort(context, "缺少{Manifest.permission.READ_PHONE_STATE}权限");
            return "";
        }
        String SimSerial=((TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        return SimSerial;
    }
    /**
     * getSerialNumber  序列号（sn）
     * Android系统2.3版本以上可以通过下面的方法得到Serial Number，且非手机设备也可以通过该接口获取。不需要权限，
     * 通用性也较高，但我测试发现红米手机返回的是 0123456789ABCDEF 明显是一个顺序的非随机字符串。也不一定靠谱。
     * @return result is same to getSerialNumber1()
     */
    public static String getSerialNumber(Context context){
        String serial = null;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ToastUtils.showShort(context, "缺少{Manifest.permission.READ_PHONE_STATE}权限");
            return "";
        }
        if (StringUtil.isEmpty(serial)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial =getSerial();
            }else {
                serial =Build.SERIAL;
            }
        }
        if (StringUtil.isEmpty(serial)){
            serial=getSerialNumber();
        }
        return serial;
    }
    /**
     * getSerialNumber  序列号（sn）
     * Android系统2.3版本以上可以通过下面的方法得到Serial Number，且非手机设备也可以通过该接口获取。不需要权限，
     * 通用性也较高，但我测试发现红米手机返回的是 0123456789ABCDEF 明显是一个顺序的非随机字符串。也不一定靠谱。
     * @return result is same to getSerialNumber1()
     */
    private static String getSerialNumber(){
        String serial = null;
        try {
            Class<?> c =Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get", String.class);
            serial = (String)get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * sms主要结构：
     　　_id：          短信序号，如100
     　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
     　　address：  发件人地址，即手机号，如+86138138000
     　　person：   发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
     　　date：       日期，long型，如1346988516，可以对日期显示格式进行设置
     　　protocol： 协议0SMS_RPOTO短信，1MMS_PROTO彩信
     　　read：      是否阅读0未读，1已读
     　　status：    短信状态-1接收，0complete,64pending,128failed
     　　type：       短信类型1是接收到的，2是已发出
     　　body：      短信具体内容
     　　service_center：短信服务中心号码编号，如+8613800755500 

     既然需要操作数据库，便少不了使用ContentResolver，所以我们应该还需要了解，短信的content uri :
     全部短信：content://sms/
     收件箱：content://sms/inbox
     发件箱：content://sms/sent
     草稿箱：content://sms/draft
     * @param context
     * @param SMS_INBOX  要获取的短信content uri  默认（content://sms/）
     * @param time       获取什么时间的短信（单位分钟）-1 不限制
     * @return
     */
    public static List<Map<String,Object>> getPhoneSms(Context context,Uri SMS_INBOX,int time) {
        List<Map<String,Object>> list=new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ToastUtils.showShort(context.getApplicationContext(), "缺少{Manifest.permission.READ_SMS}权限");
            DebugLog.i("ooc","缺少{Manifest.permission.READ_SMS}权限");
            return list;
        }
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ToastUtils.showShort(context.getApplicationContext(), "缺少{Manifest.permission.RECEIVE_SMS}权限");
            DebugLog.i("ooc","缺少{Manifest.permission.RECEIVE_SMS}权限");
            return list;
        }
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ToastUtils.showShort(context.getApplicationContext(), "缺少{Manifest.permission.RECEIVE_MMS}权限");
            DebugLog.i("ooc","缺少{Manifest.permission.RECEIVE_MMS}权限");
            return list;
        }
        if (SMS_INBOX==null){
            SMS_INBOX=Uri.parse("content://sms/");
        }
        ContentResolver cr = BaseApplication.getInstance().getContentResolver();
        String[] projection = new String[] {"_id", "address", "person","body", "date", "type" };
        Cursor cur;
        if (time<0){
            cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        }else {
            //获取10分钟之内的短信
            String where = " date >  "
                    + (System.currentTimeMillis() - time * 60 * 1000);
            cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        }
        if (null == cur) {
            DebugLog.i("ooc","************cur == null");
            return list;
        }
        while(cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("num",number);
            map.put("mess",body);
            map.put("name",name );
            list.add(map);

            // 下面匹配验证码
//            Pattern pattern = Pattern.compile("\\d{6}");
//            Matcher matcher = pattern.matcher(body);
//            if (matcher.find()) {
//                String smsCodeStr = matcher.group(0);
//                DebugLog.i("fuyanan", "sms find: code=" + matcher.group(0));// 打印出匹配到的验证码
//                break;
//            }
        }
        DebugLog.i("list="+ JSON.toJSONString(list));
        return list;
    }
    public static void main(String[] arg){
        // System.out.println("getUniqueID="+getUniqueID(this));
    }
}