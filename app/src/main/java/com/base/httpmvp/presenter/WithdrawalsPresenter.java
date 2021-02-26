package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.WithdrawalsContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：提现View(activityview)对应的Presenter
 */
public class WithdrawalsPresenter extends WithdrawalsContact.Presenter{

    public void withdrawals() {
        mView.showProgress();
        HttpMethods.getInstance().withdrawals(mGson.toJson(mView.withdrawalsParam()), mView.bindLifecycle(),new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.withdrawalsFailed(e.getMessage());
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
                    mView.withdrawalsSuccess(model.getMsg());
                }else {
                    mView.withdrawalsFailed(model.getMsg());
                }
            }
        });
    }
}