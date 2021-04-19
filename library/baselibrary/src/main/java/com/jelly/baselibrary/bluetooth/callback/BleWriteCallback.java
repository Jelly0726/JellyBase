package com.jelly.baselibrary.bluetooth.callback;


import com.jelly.baselibrary.bluetooth.exception.BleException;

public abstract class BleWriteCallback extends BleBaseCallback{

    public abstract void onWriteSuccess(int current, int total, byte[] justWrite);

    public abstract void onWriteFailure(BleException exception);

}
