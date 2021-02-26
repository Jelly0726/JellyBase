package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AddBankCartContact;
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
 * 说明：添加银行卡View(activityview)对应的Presenter
 */
public class AddBankPresenter extends AddBankCartContact.Presenter {

    @Override
    public void addBank() {
       mView.showProgress();
        Observer<HttpResult> observer=  new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.addBankFailed(e.getMessage());
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
                    mView.addBankSuccess(model.getMsg());
                }else {
                    mView.addBankFailed(model.getMsg());
                }
            }
        };
        Observable observable =  HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .addbank(GlobalToken.getToken().getToken(),mGson.toJson(mView.addBankParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        HttpMethods.getInstance().toSubscribe(observable,observer, mView.bindLifecycle());
    }
}