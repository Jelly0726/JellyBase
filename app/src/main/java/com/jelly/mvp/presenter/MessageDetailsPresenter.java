package com.jelly.mvp.presenter;


import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.model.Message;
import com.jelly.mvp.contact.MessageDetailsContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：消息详情View(activityview)对应的Presenter
 */
public class MessageDetailsPresenter extends MessageDetailsContact.Presenter {

    public void getMessageDetails() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .getMessageDetails(GlobalToken.getToken().getToken(), mGson.toJson(mView.getMessageDetailsParam()))
                .flatMap(new HttpFunctions<HttpResultData<Message>>());
        mModel.subscribe(observable, mView.bindLifecycle(),
                new ObserverResponseListener<HttpResultData<Message>>() {
                    @Override
                    public void onSuccess(HttpResultData<Message> model) {
                        mView.getMessageDetailsSuccess(model.getData());
                        mView.closeProgress();
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.closeProgress();
                        mView.getMessageDetailsFailed(msg);
                    }
                });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}