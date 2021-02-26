package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.SettingContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.model.AppVersion;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/14.
 */

public class SettingPresenter extends SettingContact.Presenter{
    @Override
    public void getAppversion() {
        mView.showProgress();
        HttpMethods.getInstance().getAppversionList(mView.bindLifecycle(),new Observer<HttpResultData<AppVersion>>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.getAppversionFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<AppVersion> model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.getAppversionSuccess(model.getMsg());
                }else {
                    mView.getAppversionFailed(model.getMsg());
                }
            }
        });
    }
}
