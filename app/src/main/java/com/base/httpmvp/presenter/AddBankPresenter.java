package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AddBankCartContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：添加银行卡View(activityview)对应的Presenter
 */
public class AddBankPresenter extends BasePresenterImpl<AddBankCartContact.View> implements AddBankCartContact.Presenter {


    public AddBankPresenter(AddBankCartContact.View interfaceView) {
        super(interfaceView);
    }

    @Override
    public void addBank(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().addbank(gson.toJson(view.addBankParam()),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.addBankFailed(isRefresh,e.getMessage());
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
                    view.addBankSuccess(isRefresh,model.getMsg());
                }else {
                    view.addBankFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}