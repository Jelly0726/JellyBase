package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.baselibrary.bankcard.BankCardInfo;
import com.jelly.mvp.contact.BankCartListContact;
import com.jelly.mvp.model.BankCartModel;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取银行卡列表View(activityview)对应的Presenter
 */
public class BankListPresenter extends BankCartListContact.Presenter {

    public void bankList(final boolean isRefresh) {
        mView.showProgress();
        Observable observable = HttpMethods
                .getInstance()
                .getProxy(IApiService.class).bankList(GlobalToken.getToken().getToken()
                        , mGson.toJson(mView.getBankListParam()))
                .flatMap(new HttpFunctions<HttpResultList<BankCardInfo>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultList<BankCardInfo>>() {
            @Override
            public void onSuccess(HttpResultList<BankCardInfo> model) {
                mView.bankListSuccess(isRefresh, model.getData());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.bankListFailed(isRefresh, msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BankCartModel();
    }
}