package com.jelly.mvp.presenter;


import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.jelly.mvp.contact.MessageContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.Message;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：消息通知View(activityview)对应的Presenter
 */
public class MessagePresenter extends MessageContact.Presenter {

    public void getMessage(final boolean isRefresh) {
        mView.showProgress();
        Observable observable = HttpMethods
                .getInstance()
                .getProxy(IApiService.class)
                .getMessage(GlobalToken.getToken().getToken(), mGson.toJson(mView.getMessageParam()))
                .flatMap(new HttpFunctions<HttpResultList<Message>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultList<Message>>() {
            @Override
            public void onSuccess(HttpResultList<Message> model) {
                mView.getMessageSuccess(isRefresh, model.getData());
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.getMessageFailed(isRefresh, msg);
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