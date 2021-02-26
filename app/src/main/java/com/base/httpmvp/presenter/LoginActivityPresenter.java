package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.LoginContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import systemdb.Login;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：登录View(activityview)对应的Presenter
 */
public class LoginActivityPresenter extends LoginContact.Presenter{
    public void userLogin() {
        mView.showProgress();
        HttpMethods.getInstance().userLogin(mGson.toJson(mView.getLoginParam()), mView.bindLifecycle(),new Observer<HttpResultData<Login>>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.loginFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<Login> model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.loginSuccess(model.getData());
                }else {
                    mView.loginFailed(model.getMsg());
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