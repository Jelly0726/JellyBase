package com.base.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.base.appManager.BaseApplication;
import com.base.log.DebugLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * 检测是否在模拟器上运行还是在真机运行
 */
public class AntiEmulator {
    private static String[] known_pipes = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };
    private static String[] known_qemu_drivers = {
            "goldfish"
    };
    private static String[] known_files = {
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
    };
    private static String[] known_numbers = {"15555215554", "15555215556",
            "15555215558", "15555215560", "15555215562", "15555215564",
            "15555215566", "15555215568", "15555215570", "15555215572",
            "15555215574", "15555215576", "15555215578", "15555215580",
            "15555215582", "15555215584",};

    private static String[] known_device_ids = {
            "000000000000000", // 默认ID
            "e21833235b6eef10", // VirusTotal id
            "012345678912345"
    };
    private static String[] known_imsi_ids = {
            "310260000000000" // 默认的 imsi id
    };
    //判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
    public static boolean isEmulator(Context context){
        try{
            DebugLog.i("是否存在已知的QEMU模拟器使用的管道:"+checkPipes());
            DebugLog.i("是否存在已知的QEMU模拟器驱动程序的列表:"+checkQEmuDriverFile());
            DebugLog.i("是否存在检测模拟器上特有的几个文件:"+CheckEmulatorFiles());
            DebugLog.i("是否存在检测模拟器默认的电话号码:"+CheckPhoneNumber(BaseApplication.getInstance()));
            DebugLog.i("检测设备IDS是不是模拟器IDS:"+CheckDeviceIDS(BaseApplication.getInstance()));
            DebugLog.i("检测imsi id是不是模拟器imsi id:"+CheckImsiIDS(BaseApplication.getInstance()));
            DebugLog.i("检测设备信息是不是模拟器:"+CheckEmulatorBuild(BaseApplication.getInstance()));
            DebugLog.i("检查是否存在已知的Genemytion环境文件:"+hasGenyFiles());
            DebugLog.i("检查是否模拟器的手机运营商:"+CheckOperatorNameAndroid(BaseApplication.getInstance()));
            DebugLog.i("检测是否存在eth0网卡:"+hasEth0Interface());
            DebugLog.i("是否存在adb.:"+hasAdbInEmulator());
            DebugLog.i("是否在跟踪应用进程.:"+hasTracerPid());
        }catch (Exception ioe) {

        }
        return false;
    }

    /**
     * 检查是否存在已知的QEMU使用的管道
     * //检测“/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道
     * @return
     */
    private static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                Log.v("Result:", "Find pipes!");
                return true;
            }
        }
        Log.i("Result:", "Not Find pipes!");
        return false;
    }

    /**
     *   检测驱动文件内容
     *   读取文件内容，然后检查已知QEmu的驱动程序的列表
     * @return
     */
    private static Boolean checkQEmuDriverFile() {
        File driver_file = new File("/proc/tty/drivers");
        if (driver_file.exists() && driver_file.canRead()) {
            byte[] data = new byte[1024];  //(int)driver_file.length()
            try {
                InputStream inStream = new FileInputStream(driver_file);
                inStream.read(data);
                inStream.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            String driver_data = new String(data);
            for (String known_qemu_driver : AntiEmulator.known_qemu_drivers) {
                if (driver_data.indexOf(known_qemu_driver) != -1) {
                    Log.i("Result:", "Find know_qemu_drivers!");
                    return true;
                }
            }
        }
        Log.i("Result:", "Not Find known_qemu_drivers!");
        return false;
    }

    /**
     * 检测模拟器上特有的几个文件
     * @return
     */
    private static Boolean CheckEmulatorFiles() {
        for (int i = 0; i < known_files.length; i++) {
            String file_name = known_files[i];
            File qemu_file = new File(file_name);
            if (qemu_file.exists()) {
                Log.v("Result:", "Find Emulator Files!");
                return true;
            }
        }
        Log.v("Result:", "Not Find Emulator Files!");
        return false;
    }

    /**
     * 检测模拟器默认的电话号码
      * @param context
     * @return
     */
    private static Boolean CheckPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("Result:", "here to request the missing permissions!");
            return false;
        }
        String phonenumber = telephonyManager.getLine1Number();

        for (String number : known_numbers) {
            if (number.equalsIgnoreCase(phonenumber)) {
                Log.v("Result:", "Find PhoneNumber!");
                return true;
            }
        }
        Log.v("Result:", "Not Find PhoneNumber!");
        return false;
    }

    /**
     * 检测设备IDS 是不是 “000000000000000”
     * @param context
     * @return
     */
    private static Boolean CheckDeviceIDS(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("Result:", "here to request the missing permissions!");
            return false;
        }
        String device_ids = telephonyManager.getDeviceId();

        for (String know_deviceid : known_device_ids) {
            if (know_deviceid.equalsIgnoreCase(device_ids)) {
                Log.v("Result:", "Find ids: 000000000000000!");
                return true;
            }
        }
        Log.v("Result:", "Not Find ids: 000000000000000!");
        return false;
    }

    /**
     * 检测imsi id是不是“310260000000000”
     * @param context
     * @return
     */
    private static Boolean CheckImsiIDS(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("Result:", "here to request the missing permissions!");
            return false;
        }
        String imsi_ids = telephonyManager.getSubscriberId();
        for (String know_imsi : known_imsi_ids) {
            if (know_imsi.equalsIgnoreCase(imsi_ids)) {
                Log.v("Result:", "Find imsi ids: 310260000000000!");
                return true;
            }
        }
        Log.v("Result:", "Not Find imsi ids: 310260000000000!");
        return false;
    }

    /**
     * 是否含有模拟器的设备信息
     * @param context
     * @return
     */
    private static Boolean CheckEmulatorBuild(Context context){
        String BOARD = android.os.Build.BOARD;
        String BOOTLOADER = android.os.Build.BOOTLOADER;
        String BRAND = android.os.Build.BRAND;
        String DEVICE = android.os.Build.DEVICE;
        String HARDWARE = android.os.Build.HARDWARE;
        String MODEL = android.os.Build.MODEL;
        String PRODUCT = android.os.Build.PRODUCT;
        if (BOARD == "unknown" || BOOTLOADER == "unknown"
                || BRAND == "generic" || DEVICE == "generic"
                || MODEL == "sdk" || PRODUCT == "sdk"
                || HARDWARE == "goldfish")
        {
            Log.v("Result:", "Find Emulator by EmulatorBuild!");
            return true;
        }
        Log.v("Result:", "Not Find Emulator by EmulatorBuild!");
        return false;
    }
    private static String[] known_geny_files = {"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    /**
     * 检查是否存在已知的Genemytion环境文件
     * @return {@code true} if any files where found to exist or {@code false} if not.
     */

    public static boolean hasGenyFiles() {
        for (String file : known_geny_files) {
            File geny_file = new File(file);
            if (geny_file.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测手机运营商家是不是模拟器运营商
     * @param context
     * @return
     */
    private static boolean CheckOperatorNameAndroid(Context context) {
        @SuppressLint("WrongConstant") String szOperatorName = ((TelephonyManager)
                context.getSystemService("phone")).getNetworkOperatorName();

        if (szOperatorName.toLowerCase().equals("android") == true) {
            Log.v("Result:", "Find Emulator by OperatorName!");
            return true;
        }
        Log.v("Result:", "Not Find Emulator by OperatorName!");
        return false;
    }
    /**
     * 检测是否存在eth0网卡.
     * @return
     */
    private static boolean hasEth0Interface() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().equals("eth0"))
                    return true;
            }
        } catch (SocketException ex) {
        }
        return false;
    }

    /**
     * 这个方法是通过读取/proc/net/tcp的信息来判断是否存在adb. 比如真机的的信息为0: 4604D20A:B512 A3D13AD8...,
     * 而模拟器上的对应信息就是0: 00000000:0016 00000000:0000, 因为adb通常是反射到0.0.0.0这个ip上,
     * 虽然端口有可能改变, 但确实是可行的.
     * @return
     */
    private static boolean hasAdbInEmulator(){
        boolean adbInEmulator = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/net/tcp")), 1000);
            String line;
            // Skip column names
            reader.readLine();
            ArrayList<tcp> tcpList = new ArrayList<tcp>();
            while ((line = reader.readLine()) != null) {
                tcpList.add(tcp.create(line.split("\\W+")));
            }
            reader.close();
            // Adb is always bounce to 0.0.0.0 - though the port can change
            // real devices should be != 127.0.0.1
            int adbPort = -1;
            for (tcp tcpItem : tcpList) {
                if (tcpItem.localIp == 0) {
                    adbPort = tcpItem.localPort;
                    break;
                }
            }
            if (adbPort != -1) {
                for (tcp tcpItem : tcpList) {
                    if ((tcpItem.localIp != 0) && (tcpItem.localPort == adbPort)) {
                        adbInEmulator = true;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return adbInEmulator;
    }
    private static class tcp {
        public int id;
        public long localIp;
        public int localPort;
        public int remoteIp;
        public int remotePort;
        static tcp create(String[] params) {
            return new tcp(params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8],
                    params[9], params[10], params[11], params[12], params[13], params[14]);
        }
        public tcp(String id, String localIp, String localPort, String remoteIp, String remotePort, String state,
                   String tx_queue, String rx_queue, String tr, String tm_when, String retrnsmt, String uid,
                   String timeout, String inode) {
            this.id = Integer.parseInt(id, 16);
            this.localIp = Long.parseLong(localIp, 16);

            this.localPort = Integer.parseInt(localPort, 16);
        }
    }
    private static String tracerpid = "TracerPid";
    /**
     * 阿里巴巴用于检测是否在跟踪应用进程
     * 容易规避, 用法是创建一个线程每3秒检测一次, 如果检测到则程序崩溃
     * @return
     */
    private static boolean hasTracerPid() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/self/status")), 1000);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > tracerpid.length()) {
                    if (line.substring(0, tracerpid.length()).equalsIgnoreCase(tracerpid)) {
                        if (Integer.decode(line.substring(tracerpid.length() + 1).trim()) > 0) {
                            return true;
                        }
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 你信或不信, 还真有许多加固程序使用这个方法...
     */
    private static boolean isBeingDebugged() {
        return Debug.isDebuggerConnected();
    }

    /**
     * 这个其实是用于检测当前操作到底是用户还是脚本在要求应用执行.
     * @return
     */
    private static boolean isUserAMonkey() {
        return ActivityManager.isUserAMonkey();
    }
}

