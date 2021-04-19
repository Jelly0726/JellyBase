package com.jelly.baselibrary.Print;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/1/5.
 * 这是小票打印机工具类
 */

public class UsbPrinterUtil {
    private static final String TAG = "UsbPrinter";
    private final Context mContext;
    private final UsbManager mUsbManager;
    private volatile List<UsbDevice> mUsbPrinterList = null;
    private static String ACTION_USB_PERMISSION = "com.posin.usbdevice.USB_PERMISSION";
    private OnUsbPermissionCallback onPermissionCallback = null;
    public static final byte[] PUSH_CASH = {0x1b, 0x70, 0x00, 0x1e, (byte) 0xff, 0x00};
    public UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointIntr;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("UsbPrinter", intent.getAction());
            if (UsbPrinterUtil.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                if (intent.getBooleanExtra("permission", false)) {
                    if (UsbPrinterUtil.this.onPermissionCallback != null) {
                        UsbPrinterUtil.this.onPermissionCallback.onUsbPermissionEvent(device, true);
                    }
                } else if (UsbPrinterUtil.this.onPermissionCallback != null) {
                    UsbPrinterUtil.this.onPermissionCallback.onUsbPermissionEvent(device, false);
                }

                context.unregisterReceiver(this);
            }

        }
    };

    public UsbPrinterUtil(Context context) {
        this.mContext = context;
        this.mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }


    /**
     * 获取所有的小票打印机设备
     */
    public List<UsbDevice> getUsbPrinterList() {

//        if (Build.MODEL.substring(0, 3).equalsIgnoreCase("TPS")) {
        if (this.mUsbPrinterList == null) {
            this.mUsbPrinterList = this.findAllUsbPrinter();
        }

        return this.mUsbPrinterList;
//        } else {
//            Log.e("_ERROR", "ERROR--->Device is not support!  This Demo just developer for TPS device");
//            return null;
//        }
    }


    public boolean requestPermission(UsbDevice usbDevice, OnUsbPermissionCallback callback) {
        if (!this.mUsbManager.hasPermission(usbDevice)) {
            IntentFilter ifilter = new IntentFilter(ACTION_USB_PERMISSION);
            this.mContext.registerReceiver(this.mReceiver, ifilter);
            PendingIntent pi = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            this.onPermissionCallback = callback;
            this.mUsbManager.requestPermission(usbDevice, pi);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取所有的小票打印机
     */
    private List<UsbDevice> findAllUsbPrinter() {
        List<UsbDevice> result = new ArrayList();
        Log.d("UsbPrinter", "find usb printer...");
        Iterator var3 = this.mUsbManager.getDeviceList().values().iterator();

        while (var3.hasNext()) {
            UsbDevice usbDevice = (UsbDevice) var3.next();
            Log.d("UsbPrinter", String.format("usb %04X:%04X : device_id=%d, device_name=%s", new Object[]{Integer.valueOf(usbDevice.getVendorId()), Integer.valueOf(usbDevice.getProductId()), Integer.valueOf(usbDevice.getDeviceId()), usbDevice.getDeviceName()}));
            if (isUsbPrinterDevice(usbDevice)) {
                Log.d("UsbPrinter", String.format("usb printer %04X:%04X : device_id=%d, device_name=%s", new Object[]{Integer.valueOf(usbDevice.getVendorId()), Integer.valueOf(usbDevice.getProductId()), Integer.valueOf(usbDevice.getDeviceId()), usbDevice.getDeviceName()}));
                result.add(usbDevice);
            }
        }

        return result;
    }

    /**
     * 识别不同的小票打印机设备
     *
     * @param usbDevice
     * @return
     */
    public static boolean isUsbPrinterDevice(UsbDevice usbDevice) {
        /**
         * getVendorId()返回一个供应商id
         *getProductId（）为设备返回一个产品ID
         * */
        int vid = usbDevice.getVendorId();
        int pid = usbDevice.getProductId();
        return vid == 5455 && pid == 5455 || vid == 26728 && pid == 1280 || vid == 26728 && pid == 1536 || vid == '衦' || vid == 1137 || vid == 1659 || vid == 1137 || vid == 1155 && pid == 1803 || vid == 17224 || vid == 7358 || vid == 6790 || vid == 1046 && pid == 20497 || vid == 10685 || vid == 4070 && pid == 33054;
    }

    /* 打开钱箱 */
    public boolean pushReceiptCash() {
        boolean canPush = false;


        if (this.sendUsbCommand(PUSH_CASH)) {
            canPush = true;
        } else {
            canPush = false;
        }
        return canPush;
    }

    //发送信息 一是打印消息，切纸，打开钱箱等
    @SuppressLint("NewApi")
    public boolean sendUsbCommand(byte[] Content) {
        boolean Result;
        synchronized (this) {
            int len = -1;
            if (mConnection != null) {
                len = mConnection.bulkTransfer(mEndpointIntr, Content, Content.length, 10000);
            }

            if (len < 0) {
                Result = false;
//                Log.i(TAG, "发送失败！ " + len);
            } else {
                Result = true;
//                Log.i(TAG, "发送" + len + "字节数据");
            }
        }
        return Result;
    }
    @SuppressLint("NewApi")
    public void setUsbDevice(UsbDevice device) {
        if (device != null) {
            UsbInterface intf = null;
            UsbEndpoint ep = null;
            int InterfaceCount = device.getInterfaceCount();
            Log.i(TAG, "InterfaceCount:" + InterfaceCount);
            int j;
//            mDevice = device;
            for (j = 0; j < InterfaceCount; j++) {
                int i;
                intf = device.getInterface(j);
                Log.i(TAG, "接口是:" + j + "类是:" + intf.getInterfaceClass());
                if (intf.getInterfaceClass() == 7) {
                    int UsbEndpointCount = intf.getEndpointCount();
                    for (i = 0; i < UsbEndpointCount; i++) {
                        ep = intf.getEndpoint(i);
                        Log.i(TAG, "端点是:" + i + "方向是:" + ep.getDirection() + "类型是:" + ep.getType());
                        if (ep.getDirection() == 0 && ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                            Log.i(TAG, "接口是:" + j + "端点是:" + i);
                            break;
                        }
                    }
                    if (i != UsbEndpointCount) {
                        break;
                    }
                }
            }
            if (j == InterfaceCount) {
                Log.i(TAG, "没有打印机接口");
                return;
            }
            mEndpointIntr = ep;
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(intf, true)) {
                Log.i(TAG, "打开成功！ ");
                Log.i(TAG, "connection " + connection);
                mConnection = connection;
            } else {
                Log.i(TAG, "打开失败！ ");
                mConnection = null;
            }
        }
    }
    @SuppressLint("NewApi")
    public void CloseReceiptUsb() {
        if (mConnection != null) {
            mConnection.close();
            mConnection = null;
        }
    }
    public interface OnUsbPermissionCallback {
        void onUsbPermissionEvent(UsbDevice var1, boolean var2);
    }
}
