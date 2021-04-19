package com.jelly.jellybase.blebluetooth.comm;


import com.jelly.baselibrary.bluetooth.data.BleDevice;

public interface Observable {

    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(BleDevice bleDevice);
}
