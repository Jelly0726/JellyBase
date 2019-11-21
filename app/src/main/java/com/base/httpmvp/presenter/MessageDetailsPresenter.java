package com.base.httpmvp.presenter;


import com.base.httpmvp.contact.MessageDetailsContact;
import com.base.httpmvp.databean.Message;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.google.gson.Gson;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：消息详情View(activityview)对应的Presenter
 */
public class MessageDetailsPresenter extends BasePresenterImpl<MessageDetailsContact.View> implements MessageDetailsContact.Presenter {


    public MessageDetailsPresenter(MessageDetailsContact.View view) {
        super(view);
    }

    public void getMessageDetails(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().getMessageDetails(new Gson().toJson(view.getMessageDetailsParam())
                ,composer,new Observer<HttpResultData<Message>>() {

                    @Override
                    public void onError(Throwable e) {
                        view.closeProgress();
                        view.getMessageDetailsFailed(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResultData<Message> model) {
                        view.closeProgress();
                        if (model.getStatus()== HttpCode.SUCCEED){
                            view.getMessageDetailsSuccess(model.getData());
                        }else {
                            view.getMessageDetailsFailed(model.getMsg());
                        }
                    }
                });
    }
}