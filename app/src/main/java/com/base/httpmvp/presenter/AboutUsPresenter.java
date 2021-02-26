package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AboutContact;
import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.AboutUs;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：关于我们View(activityview)对应的Presenter
 */
public class AboutUsPresenter extends AboutContact.Presenter {

    public void aboutUs(final boolean isRefresh) {
        mView.showProgress();
        Observer<HttpResultData<AboutUs>> observer= new Observer<HttpResultData<AboutUs>>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.aboutUsFailed(isRefresh,e.getMessage());
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
            public void onNext(HttpResultData<AboutUs> model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.aboutUsSuccess(isRefresh,model.getData());
                }else {
                    mView.aboutUsFailed(isRefresh,model.getMsg());
                }
            }
        };
        Observable observable =  HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .aboutUs(GlobalToken.getToken().getToken())
                .flatMap(new HttpFunctions<HttpResult>());
        HttpMethods.getInstance().toSubscribe(observable,observer,mView.bindLifecycle());
    }
}