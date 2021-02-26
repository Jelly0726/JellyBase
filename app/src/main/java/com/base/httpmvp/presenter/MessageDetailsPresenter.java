package com.base.httpmvp.presenter;


import com.base.httpmvp.contact.MessageDetailsContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.model.Message;
import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：消息详情View(activityview)对应的Presenter
 */
public class MessageDetailsPresenter extends MessageDetailsContact.Presenter {

    public void getMessageDetails() {
        mView.showProgress();
        HttpMethods.getInstance().getMessageDetails(new Gson().toJson(mView.getMessageDetailsParam())
                , mView.bindLifecycle(),new Observer<HttpResultData<Message>>() {

                    @Override
                    public void onError(Throwable e) {
                        mView.closeProgress();
                        mView.getMessageDetailsFailed(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResultData<Message> model) {
                        mView.closeProgress();
                        if (model.getStatus()== HttpCode.SUCCEED){
                            mView.getMessageDetailsSuccess(model.getData());
                        }else {
                            mView.getMessageDetailsFailed(model.getMsg());
                        }
                    }
                });
    }
}