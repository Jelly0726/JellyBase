package com.jelly.jellybase.blebluetooth.comm;


import com.base.bluetooth.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
