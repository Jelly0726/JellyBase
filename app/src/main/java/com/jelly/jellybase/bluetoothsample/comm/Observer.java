package com.jelly.jellybase.bluetoothsample.comm;


import com.base.bluetooth.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
