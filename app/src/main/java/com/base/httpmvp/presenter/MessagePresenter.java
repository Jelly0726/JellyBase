package com.base.httpmvp.presenter;


import com.base.httpmvp.contact.MessageContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.model.Message;
import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：消息通知View(activityview)对应的Presenter
 */
public class MessagePresenter extends MessageContact.Presenter {

    public void getMessage(final boolean isRefresh) {
        mView.showProgress();
        HttpMethods.getInstance().getMessage(new Gson().toJson(mView.getMessageParam())
                , mView.bindLifecycle(),new Observer<HttpResultList<Message>>() {

                    @Override
                    public void onError(Throwable e) {
                        mView.closeProgress();
                        mView.getMessageFailed(isRefresh,e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResultList<Message> model) {
                        mView.closeProgress();
                        if (model.getStatus()== HttpCode.SUCCEED){
                            mView.getMessageSuccess(isRefresh,model.getData());
                        }else {
                            mView.getMessageFailed(isRefresh,model.getMsg());
                        }
                    }
                });
    }
}