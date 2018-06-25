package com.jelly.jellybase.blesample.comm;


import com.base.bluetooth.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
