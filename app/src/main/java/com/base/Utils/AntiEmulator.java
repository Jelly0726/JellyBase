package com.base.Utils;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * 检测当前运行环境
 */
public class AntiEmulator {
    /**
     * 模拟器验证结果
     * 为 true 则是模拟器，false 则是真机
     */
    public static boolean verify(Context context) {
        if (
//                notHasBlueTooth()
//                || notHasLightSensorManager(context)||
                isFeatures()
                || checkIsNotRealPhone()
                || checkPipes()
                || hasEth0Interface()
                || hasAdbInEmulator()) {
            Log.i("SSSS", "检查到您的设备违规,将限制您的所有功能使用!");
            return true;
        }
        return false;
    }

    /**
     * 用途:判断蓝牙是否有效来判断是否为模拟器
     * 真机蓝牙从来没有开启过获取的名称和硬件地址可能为null 部分模拟器也可以获得名称和硬件地址
     * 返回:true 为模拟器
     */
    private static boolean notHasBlueTooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("SSSS", "设备不支持蓝牙");
            return true;
        } else {
            if (!bluetoothAdapter.isEnabled()){
                Log.i("SSSS", "设备支持蓝牙,但蓝牙未开启");
                return false;
                //未打开蓝牙，才需要打开蓝牙
//                bluetoothAdapter.enable();
            }
            //蓝牙不一定有效的。获取蓝牙名称和硬件地址，若为 null 则默认为模拟器
            String name = bluetoothAdapter.getName();
            String addree = bluetoothAdapter.getAddress();
            if (TextUtils.isEmpty(name)||TextUtils.isEmpty(addree)) {
                Log.i("SSSS", "无效蓝牙设备");
                return true;
            } else {
                Log.i("SSSS", "有效蓝牙设备");
                return false;
            }
        }
    }

    /**
     * 用途:依据是否存在光传感器来判断是否为模拟器(小部分手机没有光传感器)
     * 返回:true 为模拟器
     */
    private static boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor == null) {
            Log.i("SSSS", "不存在光传感器");
            return true;
        } else {
            Log.i("SSSS", "存在光传感器");
            return false;
        }
    }
    /**
     *用途:根据部分特征参数设备信息来判断是否为模拟器
     *返回:true 为模拟器
     */
    private static boolean isFeatures() {
        boolean isf=Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
        if (isf){
            Log.i("SSSS", "存在模拟器特征参数");
        }else {
            Log.i("SSSS", "不存在模拟器特征参数");
        }
        return isf;
    }
    /**
     *根据CPU是否为电脑来判断是否为模拟器
     *返回:true 为模拟器
     */
    private static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            Log.i("SSSS", "CPU为电脑CPU");
            return true;
        }
        Log.i("SSSS", "CPU不是电脑CPU");
        return false;
    }

    /**
     * 根据 CPU 是否为电脑来判断是否为模拟器
     *
     * @return
     */
    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = processBuilder.start();
            StringBuffer stringBuffer = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                stringBuffer.append(readLine);
            }
            responseReader.close();
            result = stringBuffer.toString().toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检测是否存在eth0(光纤以太网接口卡)网卡.
     *
     * @return
     */
    private static boolean hasEth0Interface() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().equals("eth0")) {
                    Log.i("SSSS", "存在eth0(光纤以太网接口卡)网卡");
                    return true;
                }
            }
        } catch (SocketException ex) {
        }
        Log.i("SSSS", "不存在eth0(光纤以太网接口卡)网卡");
        return false;
    }

    /**
     * 这个方法是通过读取/proc/net/tcp的信息来判断是否存在adb. 比如真机的的信息为0: 4604D20A:B512 A3D13AD8...,
     * 而模拟器上的对应信息就是0: 00000000:0016 00000000:0000, 因为adb通常是反射到0.0.0.0这个ip上,
     * 虽然端口有可能改变, 但确实是可行的.
     *
     * @return
     */
    private static boolean hasAdbInEmulator() {
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
        if (adbInEmulator){
            Log.i("SSSS", "存在adb");
        }else {
            Log.i("SSSS", "不存在adb");
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

    /**
     * 这个其实是用于检测当前操作到底是用户还是脚本在要求应用执行.
     *
     * @return
     */
    private static boolean isUserAMonkey() {
        return ActivityManager.isUserAMonkey();
    }
    /**
     *用途:检测模拟器的特有文件
     *返回:true 为模拟器
     */
    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};

    private static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                Log.i("SSSS", "存在模拟器的特有文件");
                return true;
            }
        }
        Log.i("SSSS", "不存在模拟器的特有文件");
        return false;
    }

}

