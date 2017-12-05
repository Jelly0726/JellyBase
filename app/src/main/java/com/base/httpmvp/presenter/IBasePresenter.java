package com.base.httpmvp.presenter;


import com.base.httpmvp.mode.business.Business;
import com.base.httpmvp.mode.business.IBusiness;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 *  * 说明：Presenter接口
 */

public interface IBasePresenter{
    public IBusiness mIBusiness = new Business();

    //默认初始化
    void start();

    //Activity关闭把view对象置为空
    void detach();

    //将网络请求的每一个disposable添加进入CompositeDisposable，再退出时候一并注销
    void addDisposable(Disposable subscription);

    //注销所有请求
    void unDisposable();
}
