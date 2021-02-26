package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.BankCartContact;
import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.token.GlobalToken;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：删除银行卡View(activityview)对应的Presenter
 */
public class BankCartPresenter extends BankCartContact.Presenter {

    @Override
    public void deletebank() {
        mView.showProgress();
        Observer<HttpResult> observer= new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.deletebankFailed(e.getMessage());
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
                    mView.deletebankSuccess(model.getMsg());
                }else {
                    mView.deletebankFailed(model.getMsg());
                }
            }
        };
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .deletebank(GlobalToken.getToken().getToken(),mGson.toJson(mView.deletebankParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        HttpMethods.getInstance().toSubscribe(observable,observer, mView.bindLifecycle());
    }
}