package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.jelly.mvp.contact.RegisterContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter extends RegisterContact.Presenter {
    public void userRegister() {
        mView.showProgress();
        Observable observable =  HttpMethods.getInstance()
                .getProxy(IApiService.class).userRegistration(mGson.toJson(mView.getRegParam()))
                //.map(new HttpResultFunc<List<HttpResult>>());
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.excuteSuccess(model);
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.excuteFailed(msg);
                mView.closeProgress();
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
                mView.verifiCodeFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}