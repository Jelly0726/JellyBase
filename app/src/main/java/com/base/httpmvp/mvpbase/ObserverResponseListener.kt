package com.base.httpmvp.mvpbase

/**
 * mvp中model的回调监听接口
 * @param <T>
</T> */
interface ObserverResponseListener<T> {
    fun onSuccess(model: T)
    fun onFailure(msg: String)
}