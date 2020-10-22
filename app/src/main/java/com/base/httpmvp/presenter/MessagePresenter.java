package com.base.httpmvp.presenter;


import com.base.httpmvp.contact.MessageContact;
import com.base.model.Message;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.google.gson.Gson;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：消息通知View(activityview)对应的Presenter
 */
public class MessagePresenter extends BasePresenterImpl<MessageContact.View> implements MessageContact.Presenter {


    public MessagePresenter(MessageContact.View view) {
    super(view);
    }

    public void getMessage(final boolean isRefresh,ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().getMessage(new Gson().toJson(view.getMessageParam())
                ,composer,new Observer<HttpResultList<Message>>() {

                    @Override
                    public void onError(Throwable e) {
                        view.closeProgress();
                        view.getMessageFailed(isRefresh,e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResultList<Message> model) {
                        view.closeProgress();
                        if (model.getStatus()== HttpCode.SUCCEED){
                            view.getMessageSuccess(isRefresh,model.getData());
                        }else {
                            view.getMessageFailed(isRefresh,model.getMsg());
                        }
                    }
                });
    }
}