package com.jelly.jellybase.blesample.comm;


import com.base.bluetooth.data.BleDevice;

public interface Observable {

    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(BleDevice bleDevice);
}
