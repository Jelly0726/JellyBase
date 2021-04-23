package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.jelly.mvp.contact.LoginContact;

import io.reactivex.Observable;
import systemdb.Login;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：登录View(activityview)对应的Presenter
 */
public class LoginActivityPresenter extends LoginContact.Presenter {
    public void userLogin() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .userLogin(mGson.toJson(mView.getLoginParam()))
                .flatMap(new HttpFunctions<HttpResultData<Login>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultData<Login>>() {
            @Override
            public void onSuccess(HttpResultData<Login> model) {
                mView.loginSuccess(model.getData());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.closeProgress();
                mView.loginFailed(msg);
            }
        });
    }

    public void getVerifiCode() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class).getVerifiCode(mGson.toJson(mView.getVerifiCodeParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.verifiCodeSuccess(model);
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}