package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AddressContact;
import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.jellybase.datamodel.RecevierAddress;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：收货地址View(activityview)对应的Presenter
 */
public class AddressPresenter extends AddressContact.Presenter {


    @Override
    public void operaAddress() {
        mView.showProgress();

        Observer<HttpResult> observer= new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.operaAddressFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResult model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.operaAddressSuccess(model.getMsg());
                }else {
                    mView.operaAddressFailed(model.getMsg());
                }
            }
        };
        Observable observable =  HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .operaAddress(GlobalToken.getToken().getToken(),mGson.toJson(mView.operaAddressParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        HttpMethods
                .getInstance()
                .toSubscribe(observable,observer,mView.bindLifecycle());
    }
    @Override
    public void getAddressList(final boolean isRefresh) {
        mView.showProgress();

        Observer<HttpResultList<RecevierAddress>> observer=new Observer<HttpResultList<RecevierAddress>>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.getAddressListFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultList<RecevierAddress> model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.getAddressListSuccess(isRefresh,model.getData());
                }else {
                    mView.getAddressListFailed(isRefresh,model.getMsg());
                }
            }
        };
        Observable observable =  HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .getAddressList(GlobalToken.getToken().getToken(),mGson.toJson(mView.getAddressListParam()))
                .flatMap(new HttpFunctions<HttpResultList<RecevierAddress>>());
        HttpMethods.getInstance().toSubscribe(observable,observer, mView.bindLifecycle());
    }
}