package com.jelly.jellybase.bluetooth.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothUtil {
    private final static String TAG = "BluetoothUtil";

    // 获取蓝牙的开关状态
    public static boolean getBlueToothStatus(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled = false;
        switch (bluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_TURNING_ON:
                enabled = true;
                break;
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
            default:
                enabled = false;
                break;
        }
        return enabled;
    }

    // 打开或关闭蓝牙
    public static void setBlueToothStatus(Context context, boolean enabled) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (enabled == true) {
            bluetoothAdapter.enable();
        } else {
            bluetoothAdapter.disable();
        }
    }

    public static String readInputStream(InputStream inStream) {
        String result = "";
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            inStream.close();
            result = new String(data, "utf8");
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    public static void writeOutputStream(BluetoothSocket socket, String message) {
        Log.d(TAG, "begin writeOutputStream message=" + message);
        try {
            OutputStream outStream = socket.getOutputStream();
            outStream.write(message.getBytes());
            //outStream.flush();
            //outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "end writeOutputStream");
    }
    /**
     * read Rssi
     *
     * @param bleDevice
     * @param callback
     */
    public static void readRssi(Context context,BluetoothDevice bleDevice,
                                BluetoothGattCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BluetoothGattCallback can not be Null!");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bleDevice.connectGatt(context, false, callback);
        }
    }

    /**
     * 功能：根据rssi计算距离 返回数据单位为m
     * 计算公式：
     d = 10^((abs(RSSI) - A) / (10 * n))
     其中：
     d - 计算所得距离
     RSSI - 接收信号强度（负值）
     A - 发射端和接收端相隔1米时的信号强度
     n - 环境衰减因子
     * @param rssi
     * @return
     */
    //A和n的值，需要根据实际环境进行检测得出
    private static final double A_Value=50;/**A - 发射端和接收端相隔1米时的信号强度*/
    private static final double n_Value=2.5;/** n - 环境衰减因子*/
    public static double getDistance(int rssi){
        int iRssi = Math.abs(rssi);
        double power = (iRssi-A_Value)/(10*n_Value);
        return Math.pow(10,power);
    }
}
