package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.mvp.contact.CancelOrderContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：取消订单View(activityview)对应的Presenter
 */
public class CancelOrderPresenter extends CancelOrderContact.Presenter {

    public void cancelOrder() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance().getProxy(IApiService.class)
                .cancelOrder(GlobalToken.getToken().getToken()
                        , mGson.toJson(mView.cancelOrderParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.cancelOrderSuccess(model.getMsg());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.cancelOrderFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}