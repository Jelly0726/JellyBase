package com.base.httpmvp.mvpPresenter;

import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.mvpContact.SettingContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.AppVersion;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/14.
 */

public class SettingPresenter extends SettingContact.Presenter {
    @Override
    public void getAppversion() {
        mView.showProgress();
        Observable observable =  HttpMethods.getInstance().
                getProxy(IApiService.class).getAppversionList(GlobalToken.getToken().getToken())
                .flatMap(new HttpFunctions<HttpResultData<AppVersion>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultData<AppVersion>>() {
            @Override
            public void onSuccess(HttpResultData<AppVersion> model) {
                mView.getAppversionSuccess(model.getMsg());
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.getAppversionFailed(msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}
