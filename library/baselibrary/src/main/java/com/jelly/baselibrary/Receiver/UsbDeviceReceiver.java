package com.jelly.baselibrary.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.USB_SERVICE;


/**
 *USB事件监听
 mReceiver = new UsbDeviceEventReceiver();
 IntentFilter filter = new IntentFilter();
 filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
 filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
 registerReceiver(mReceiver, filter);
 */
public class UsbDeviceReceiver extends BroadcastReceiver {
    private static final String TAG = UsbDeviceReceiver.class.getSimpleName();

    public static final String TAGLISTEN = "android.intent.action.HEADSET_PLUG";
    private final static String TAGUSB = "android.hardware.usb.action.USB_STATE";
    public static final String TAGIN = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String TAGOUT = "android.hardware.usb.action.USB_DEVICE_DETACHED";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(TAGLISTEN)) {
            Log.i(TAG, "TAGLISTEN");
        } else if (action.equals(TAGUSB)) {
            Log.i(TAG, "TAGUSB");
        } else if (action.equals(TAGIN)) {
            Log.i(TAG, "TAGIN");
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (null == device) {
                Log.i(TAG, "null usb device");
                return;
            }
            int count = device.getConfigurationCount();
            boolean hasAudio = false;
            for (int i = 0; i < count; i++) {
                UsbConfiguration configuration = device.getConfiguration(i);
                if (null == configuration) {
                    Log.i(TAG, "null usb configuration");
                    return;
                }
                int interfaceCount = configuration.getInterfaceCount();
                for (int j = 0; j < interfaceCount; j++) {
                    UsbInterface usbInterface = configuration.getInterface(j);
                    if (null != usbInterface && usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_AUDIO) {
                        hasAudio = true;
                    }
                }
            }
            Log.i(TAG, "has audio:" + hasAudio);
            // your operation here
        } else if (action.equals(TAGOUT)) {
            Log.i(TAG, "TAGOUT");
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (null == device) {
                Log.i(TAG, "null usb device");
                return;
            }
            int count = device.getConfigurationCount();
            boolean hasAudio = false;
            for (int i = 0; i < count; i++) {
                UsbConfiguration configuration = device.getConfiguration(i);
                if (null == configuration) {
                    Log.i(TAG, "null usb configuration");
                    return;
                }
                int interfaceCount = configuration.getInterfaceCount();
                for (int j = 0; j < interfaceCount; j++) {
                    UsbInterface usbInterface = configuration.getInterface(j);
                    if (null != usbInterface && usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_AUDIO) {
                        hasAudio = true;
                    }
                }
            }
            // your operation here
        }
    }
    //UsbManager检测
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean detectUsbAudioDevice(Context context) {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) context.getApplicationContext().getSystemService(USB_SERVICE)).getDeviceList();
        for (Map.Entry entry : deviceHashMap.entrySet()) {
            UsbDevice device = (UsbDevice) entry.getValue();
            if (device==null)continue;
            for (int i = 0; i < device.getConfigurationCount(); i++) {
                UsbConfiguration configuration = device.getConfiguration(i);
                if (null == configuration) {
                    continue;
                }
                for (int j = 0; j < configuration.getInterfaceCount(); j++) {
                    UsbInterface usbInterface = configuration.getInterface(j);
                    if (null == usbInterface) {
                        continue;
                    }
                    if (UsbConstants.USB_CLASS_AUDIO == usbInterface.getInterfaceClass()) {
                        Log.i(TAG, "has usb audio device");
                        return true;
                    }
                }
            }
        }
        Log.i(TAG, "have no usb audio device");
        return false;
    }
}