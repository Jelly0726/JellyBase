package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.ForgetPwdContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：忘记密码View(activityview)对应的Presenter
 */
public class ForgetPasswordPresenter extends ForgetPwdContact.Presenter{

    public void forgetPwd() {
        mView.showProgress();
        HttpMethods.getInstance().forgetPassword(mGson.toJson(mView.forgetPasswordParam()), mView.bindLifecycle(),new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.forgetPasswordFailed(e.getMessage());
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
                    mView.forgetPasswordSuccess(model.getMsg());
                }else {
                    mView.forgetPasswordFailed(model.getMsg());
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