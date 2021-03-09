package com.jelly.mvp.presenter;

import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.jelly.mvp.contact.SetPwdContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：设置密码View(activityview)对应的Presenter
 */
public class SetPassWordActivityPresenter extends SetPwdContact.Presenter{

    @Override
    public void setPassword() {
        mView.showProgress();
        Observable observable =  HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .setPassWord(mGson.toJson(mView.getSetPassWordParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.excuteSuccess(model.getMsg());
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.excuteFailed( msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
addDisposable(this.hashCode(),disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel=new BaseModel();
    }
}