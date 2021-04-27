package com.jelly.mvp.presenter;


import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.model.Message;
import com.jelly.mvp.contact.MessageContact;

import io.reactivex.Observable;

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
            }

            @Override
            public void onFailure(String msg) {
                mView.getMessageFailed(isRefresh, msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}