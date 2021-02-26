package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.CancelOrderContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：取消订单View(activityview)对应的Presenter
 */
public class CancelOrderPresenter extends CancelOrderContact.Presenter{

    public void cancelOrder() {
        mView.showProgress();
        HttpMethods.getInstance().cancelOrder(mGson.toJson(mView.cancelOrderParam()), mView.bindLifecycle(),new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.cancelOrderFailed(e.getMessage());
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
                    mView.cancelOrderSuccess(model.getMsg());
                }else {
                    mView.cancelOrderFailed(model.getMsg());
                }
            }
        });
    }
}