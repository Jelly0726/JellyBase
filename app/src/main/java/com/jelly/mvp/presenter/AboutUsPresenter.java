package com.jelly.mvp.presenter;

import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.jelly.mvp.contact.AboutContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.AboutUs;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

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
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.closeProgress();
                mView.aboutUsFailed(isRefresh,msg);
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable subscription) {
                addDisposable(this.hashCode(),subscription);
            }
        });
    }

    @Override
    public void start() {
        mModel=new BaseModel();
    }
}