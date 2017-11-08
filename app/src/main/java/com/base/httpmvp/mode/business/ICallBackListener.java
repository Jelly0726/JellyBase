package com.base.httpmvp.mode.business;

/**
 * Created by Administrator on 2017/11/8.
 */
public interface ICallBackListener<T> {

    public void onSuccess(T mCallBackVo);
    public void onFaild(String message);

}