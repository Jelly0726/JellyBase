package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.model.AboutUs;
import com.jelly.mvp.contact.AboutContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：关于我们View(activityview)对应的Presenter
 */
public class AboutUsPresenter extends AboutContact.Presenter {
    public void aboutUs(final boolean isRefresh) {
        mView.showProgress();
        Observable observable =  HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .aboutUs(GlobalToken.getToken().getToken())
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable,mView.bindLifecycle(), new ObserverResponseListener<HttpResultData<AboutUs>>(){

            @Override
            public void onSuccess(HttpResultData<AboutUs> model) {
                mView.closeProgress();
                mView.aboutUsSuccess(isRefresh,model.getData());
            }

            @Override
            public void onFailure(String msg) {
                mView.closeProgress();
                mView.aboutUsFailed(isRefresh,msg);
            }
        });
    }

    @Override
    public void start() {
        mModel=new BaseModel();
    }
}