package com.jelly.mvp.presenter;


import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.jelly.mvp.contact.MessageDetailsContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.baselibrary.model.Message;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

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
                        removeDisposable(this.hashCode());
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.closeProgress();
                        removeDisposable(this.hashCode());
                        mView.getMessageDetailsFailed(msg);
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