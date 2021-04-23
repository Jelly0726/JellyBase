package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.mvp.contact.OperaAddressContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：配置收货地址View(activityview)对应的Presenter
 */
public class OperaAddressPresenter extends OperaAddressContact.Presenter {
    @Override
    public void operaAddress() {
        mView.showProgress();
        Observable observable = HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .operaAddress(GlobalToken.getToken().getToken(), mGson.toJson(mView.operaAddressParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.operaAddressSuccess(model.getMsg());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.operaAddressFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}