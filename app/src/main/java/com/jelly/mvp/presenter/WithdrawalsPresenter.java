package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.mvp.contact.WithdrawalsContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：提现View(activityview)对应的Presenter
 */
public class WithdrawalsPresenter extends WithdrawalsContact.Presenter {

    public void withdrawals() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance().getProxy(IApiService.class)
                .withdrawals(GlobalToken.getToken().getToken(), mGson.toJson(mView.withdrawalsParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.withdrawalsSuccess(model.getMsg());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.withdrawalsFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel=new BaseModel();
    }
}