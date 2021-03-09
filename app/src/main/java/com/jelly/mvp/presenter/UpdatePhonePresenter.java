package com.jelly.mvp.presenter;

import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.jelly.mvp.contact.UpdatePhoneContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.token.GlobalToken;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改手机号View(activityview)对应的Presenter
 */
public class UpdatePhonePresenter extends UpdatePhoneContact.Presenter {

    public void updatePhone() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class).updatePhone(GlobalToken.getToken().getToken(), mGson.toJson(mView.getUpdatePhoneParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.updatePhoneSuccess(model);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.updatePhoneFailed(msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    public void getVerifiCode() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class).getVerifiCode(mGson.toJson(mView.getVerifiCodeParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.verifiCodeSuccess(model);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.verifiCodeFailed(msg);
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
        mModel=new BaseModel();
    }
}