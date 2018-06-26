package com.jelly.jellybase.bluetooth.bean;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BlueDevice {
    private BluetoothDevice mDevice;
    private int state;
    /**
     * 一般情况下，经典蓝牙强度
     -50 ~ 0dBm 信号强
     -70 ~-50dBm信号中
     <-70dBm      信号弱
     低功耗蓝牙分四级
     -60 ~ 0   4
     -70 ~ -60 3
     -80 ~ -70  2
     <-80 1
     */
    private int mRssi;
    private BluetoothSocket socket;
    public BlueDevice(BluetoothDevice mDevice, int state,int mRssi) {
        this.mDevice = mDevice;
        this.state = state;
        this.mRssi = mRssi;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public String getName() {
        if (mDevice!=null)
            return mDevice.getName();
        return "";
    }


    public String getAddress() {
        if (mDevice!=null)
            return mDevice.getAddress();
        return "";
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getmRssi() {
        return mRssi;
    }

    public void setmRssi(int mRssi) {
        this.mRssi = mRssi;
    }

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }
}
