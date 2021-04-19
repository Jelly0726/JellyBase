package com.jelly.baselibrary.bluetooth.callback;


import com.jelly.baselibrary.bluetooth.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}