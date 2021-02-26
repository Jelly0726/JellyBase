package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.SetPwdContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：设置密码View(activityview)对应的Presenter
 */
public class SetPassWordActivityPresenter extends SetPwdContact.Presenter{

    @Override
    public void setPassword() {
        mView.showProgress();
        HttpMethods.getInstance().setPassWord(mGson.toJson(mView.getSetPassWordParam()),
                mView.bindLifecycle(),new Observer<HttpResult>() {

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
                    mView.excuteSuccess(model.getMsg());
                }else {
                    mView.excuteFailed( model.getMsg());
                }
            }
        });
    }
}