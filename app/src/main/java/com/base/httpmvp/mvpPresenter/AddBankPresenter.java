package com.base.httpmvp.mvpPresenter;

import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.mvpContact.AddBankCartContact;
import com.base.httpmvp.mvpModel.BankCartModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.token.GlobalToken;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

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
                removeDisposable(this.hashCode());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.addBankFailed(msg);
                removeDisposable(this.hashCode());
                mView.closeProgress();
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(),disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel=new BankCartModel();
    }
}