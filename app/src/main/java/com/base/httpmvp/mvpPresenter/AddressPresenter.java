package com.base.httpmvp.mvpPresenter;

import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.mvpContact.AddressContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.jellybase.datamodel.RecevierAddress;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：收货地址View(activityview)对应的Presenter
 */
public class AddressPresenter extends AddressContact.Presenter {


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
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.operaAddressFailed(msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());

            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    @Override
    public void getAddressList(final boolean isRefresh) {
        mView.showProgress();
        Observable observable = HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .getAddressList(GlobalToken.getToken().getToken(), mGson.toJson(mView.getAddressListParam()))
                .flatMap(new HttpFunctions<HttpResultList<RecevierAddress>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultList<RecevierAddress>>() {
            @Override
            public void onSuccess(HttpResultList<RecevierAddress> model) {
                mView.getAddressListSuccess(isRefresh, model.getData());
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.getAddressListFailed(isRefresh, msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());

            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}