package com.base.Print;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.base.appManager.ExecutorManager;
import com.base.toast.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * USBPrinter usbPrinter = USBPrinter.getInstance();
 * usbPrinter.initPrinter(this);
 * usbPrinter.bold(true);
 * usbPrinter.setTextSize(3);
 * usbPrinter.setAlign(1);
 * usbPrinter.printTextNewLine(tableName);
 * usbPrinter.printLine(1);
 * usbPrinter.bold(false);
 * usbPrinter.printTextNewLine("出品单号："+bean.getKitExpNumber());
 * usbPrinter.printTextNewLine("出品员："+bean.getChefName());
 * usbPrinter.printBarCode("6936983800013");
 * usbPrinter.printLine(5);
 * usbPrinter.cutPager();
 *
 * 记得在Actvity的onDestroy生命周期里调用USBPrinter.getInstance().close()方法
 *
 * android设备连接USB打印机的打印方法
 *
 *  * 80热敏纸最大打印宽度 292.0
 *  * 一个空格宽度 3.0
 *  * 一个中午宽度 12.0
 *  * 一个数字宽度 7.0
 *  * 一个中午符号 12.0 （：）
 *  * 一个英文符号 3.0  （.）
 */
public class USBPrinter {
    public static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";

    private static USBPrinter mInstance;

    private Context mContext;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;

    private UsbEndpoint ep,printerEp;
    private UsbInterface usbInterface;

    private static final int TIME_OUT = 100000;

    public static USBPrinter getInstance() {
        if (mInstance == null) {
            synchronized (USBPrinter.class) {
                if (mInstance == null) {
                    mInstance = new USBPrinter();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public static void initPrinter(Context context) {
        getInstance().init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init(Context context) {
//        list.clear();
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, filter);

        // 列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        for (UsbDevice device : deviceList.values()) {
            usbInterface = device.getInterface(0);
            if (usbInterface.getInterfaceClass() == 7) {
                Log.d("device", device.getProductName() + "     " + device.getManufacturerName());
                Log.d("device", device.getVendorId() + "     " + device.getProductId() + "      " + device.getDeviceId());
                Log.d("device", usbInterface.getInterfaceClass() + "");
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                }else {
                    connectUsbPrinter(device);
                }
            }
        }

    }

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("action", action);
            UsbDevice mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUsbDevice != null) {
                        Log.d("receiver", action);
                        connectUsbPrinter(mUsbDevice);
                    } else {
                        ToastUtils.showShort(context, "USB设备请求被拒绝");
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    ToastUtils.showShort(context, "有设备拔出");
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                ToastUtils.showShort(context, "有设备插入");
                if (mUsbDevice != null) {
                    if (!mUsbManager.hasPermission(mUsbDevice)) {
                        mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
                    }
                }
            }
        }
    };

    public void close() {
        if(mUsbDeviceConnection != null){
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }
        mContext.unregisterReceiver(mUsbDeviceReceiver);
        mContext = null;
        mUsbManager = null;
    }

    private void connectUsbPrinter(UsbDevice mUsbDevice) {
        if (mUsbDevice != null) {
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                ep = usbInterface.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                        printerEp = ep;
                        if (mUsbDeviceConnection != null) {
                            ToastUtils.showShort(mContext, "设备已连接");
                            mUsbDeviceConnection.claimInterface(usbInterface, true);
                            mUsbDeviceConnection.releaseInterface(usbInterface);
                        }
                    }
                }
            }
        } else {
            ToastUtils.showShort(mContext, "未发现可用的打印机");

        }
    }

    private void write(final byte[] bytes) {
        ExecutorManager.getInstance().getCachedThread().execute(new Runnable() {
            @Override
            public void run() {
                if (mUsbDeviceConnection != null) {
                    int b = mUsbDeviceConnection.bulkTransfer(printerEp, bytes, bytes.length, TIME_OUT);
                    Log.i("Return Status", "b-->" + b);
                } else {
                    Looper.prepare();
                    handler.sendEmptyMessage(0);
                    Looper.loop();
                }
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastUtils.showShort(mContext, "未发现可用的打印机");
        }
    };

    /**
     * 打印文字
     * @param msg
     */
    public void printText(String msg) {
        byte[] bytes = new byte[0];
        try {
            bytes = msg.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write(bytes);
    }

    /**
     * 换行打印文字
     * @param msg
     */
    public void printTextNewLine(String msg) {
        byte[] bytes = new byte[0];
        try {
            bytes = msg.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write(new String("\n").getBytes());
        write(bytes);
    }

    /**
     * 打印空行
     * @param size
     */
    public void printLine(int size) {
        for (int i = 0; i < size; i++) {
            printText("\n");
        }
    }

    /**
     * 设置字体大小
     * @param size 0:正常大小 1:两倍高 2:两倍宽 3:两倍大小 4:三倍高 5:三倍宽 6:三倍大 7:四倍高 8:四倍宽 9:四倍大小 10:五倍高 11:五倍宽 12:五倍大小
     */
    public void setTextSize(int size) {
        write(ESCUtil.setTextSize(size));
    }

    /**
     * 字体加粗
     * @param isBold
     */
    public void bold(boolean isBold) {
        if (isBold) write(ESCUtil.boldOn());
        else write(ESCUtil.boldOff());
    }

    /**
     * 打印一维条形码
     * @param data
     */
    public void printBarCode(String data) {
        write(ESCUtil.getPrintBarCode(data, 5, 90, 5, 2));
    }

    /**
     * 设置对齐方式
     * @param position
     */
    public void setAlign(int position) {
        byte[] bytes = null;
        switch (position) {
            case 0:
                bytes = ESCUtil.alignLeft();
                break;
            case 1:
                bytes = ESCUtil.alignCenter();
                break;
            case 2:
                bytes = ESCUtil.alignRight();
                break;
        }
        write(bytes);
    }

    /**
     * 切纸
     */
    public void cutPager() {
        write(ESCUtil.cutter());
    }

}