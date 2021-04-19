package com.jelly.baselibrary.appManager;

/**
 * 观察者，我们称它为Observer，有时候我们也称它为订阅者，即Subscriber
 * @author lijie
 */
public interface Observer<T> {
    public void onUpdate(Observable<T> observable, T data);
}
