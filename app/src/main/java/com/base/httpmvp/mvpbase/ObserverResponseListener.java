package com.base.httpmvp.mvpbase;

/**
 * mvp中model的回调监听接口
 * @param <T>
 */
public interface ObserverResponseListener<T> {
    public void onSuccess(T model);
    public void onFailure(String msg);
}
