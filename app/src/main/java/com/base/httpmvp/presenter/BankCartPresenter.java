package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.BankCartContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：删除银行卡View(activityview)对应的Presenter
 */
public class BankCartPresenter extends BasePresenterImpl<BankCartContact.View> implements BankCartContact.Presenter {


    public BankCartPresenter(BankCartContact.View interfaceView) {
        super(interfaceView);
    }

    @Override
    public void deletebank( ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().deletebank(gson.toJson(view.deletebankParam()),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.deletebankFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResult model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.deletebankSuccess(model.getMsg());
                }else {
                    view.deletebankFailed(model.getMsg());
                }
            }
        });
    }
}