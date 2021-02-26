package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.RegisterContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter extends RegisterContact.Presenter{
    public void userRegister() {
        mView.showProgress();
        HttpMethods.getInstance().userRegistration(mGson.toJson(mView.getRegParam()), mView.bindLifecycle()
                ,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.excuteFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                mView.closeProgress();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResult model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.excuteSuccess(model);
                }else {
                    mView.excuteFailed(model.getMsg());
                }

            }
        });
    }
    public void getVerifiCode() {
        mView.showProgress();
        HttpMethods.getInstance().getVerifiCode(mGson.toJson(mView.getVerifiCodeParam())
                , mView.bindLifecycle(),new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.verifiCodeFailed(e.getMessage());
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
                    mView.verifiCodeSuccess(model);
                }else {
                    mView.verifiCodeFailed(model.getMsg());
                }
            }
        });
    }
}