package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.model.AppVersion;
import com.jelly.mvp.contact.SettingContact;

import io.reactivex.Observable;

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
            }

            @Override
            public void onFailure(String msg) {
                mView.getAppversionFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}
