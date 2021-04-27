package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.mvp.contact.BankCartContact;
import com.jelly.mvp.model.BankCartModel;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：删除银行卡View(activityview)对应的Presenter
 */
public class BankCartPresenter extends BankCartContact.Presenter {
    @Override
    public void deletebank() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .deletebank(GlobalToken.getToken().getToken(),mGson.toJson(mView.deletebankParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.closeProgress();
                mView.deletebankSuccess(model.getMsg());
            }

            @Override
            public void onFailure(String msg) {
                mView.closeProgress();
                mView.deletebankFailed(msg);
            }

        });
    }

    @Override
    public void start() {
        mModel=new BankCartModel();
    }
}