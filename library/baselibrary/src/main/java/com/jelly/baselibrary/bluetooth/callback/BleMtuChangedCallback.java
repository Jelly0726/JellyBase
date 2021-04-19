package com.jelly.baselibrary.bluetooth.callback;


import com.jelly.baselibrary.bluetooth.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
