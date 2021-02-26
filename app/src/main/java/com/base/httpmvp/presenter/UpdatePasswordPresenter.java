package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.UpdataPwdContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改密码View(activityview)对应的Presenter
 */
public class UpdatePasswordPresenter extends UpdataPwdContact.Presenter {

    public void updatePassword() {
        mView.showProgress();
        HttpMethods.getInstance().updatePassword(mGson.toJson(mView.getUpdatePasswordParam())
                , mView.bindLifecycle(),new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.updatePasswordFailed(e.getMessage());
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
                    mView.updatePasswordSuccess(model.getMsg());
                }else {
                    mView.updatePasswordFailed(model.getMsg());
                }
            }
        });
    }
}