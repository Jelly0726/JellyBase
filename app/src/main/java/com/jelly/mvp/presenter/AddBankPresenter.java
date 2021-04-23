package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.mvp.contact.AddBankCartContact;
import com.jelly.mvp.model.BankCartModel;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：添加银行卡View(activityview)对应的Presenter
 */
public class AddBankPresenter extends AddBankCartContact.Presenter {

    @Override
    public void addBank() {
       mView.showProgress();
        Observable observable =  HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .addbank(GlobalToken.getToken().getToken(),mGson.toJson(mView.addBankParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.addBankSuccess(model.getMsg());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.addBankFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel=new BankCartModel();
    }
}