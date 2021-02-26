package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.OperaAddressContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：配置收货地址View(activityview)对应的Presenter
 */
public class OperaAddressPresenter extends OperaAddressContact.Presenter {
    @Override
    public void operaAddress() {
        mView.showProgress();
        HttpMethods.getInstance().operaAddress(mGson.toJson(mView.operaAddressParam()), mView.bindLifecycle(),new Observer<HttpResult>() {

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
        });
    }
}