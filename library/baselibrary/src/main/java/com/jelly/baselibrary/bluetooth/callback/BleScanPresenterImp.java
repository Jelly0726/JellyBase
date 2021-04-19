package com.jelly.baselibrary.bluetooth.callback;


import com.jelly.baselibrary.bluetooth.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
