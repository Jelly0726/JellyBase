package com.jelly.jellybase.blebluetooth.comm;


import com.jelly.baselibrary.bluetooth.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
