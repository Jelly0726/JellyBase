package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.WithdrawalsContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：提现View(activityview)对应的Presenter
 */
public class WithdrawalsPresenter extends BasePresenterImpl<WithdrawalsContact.View>
        implements WithdrawalsContact.Presenter{


    public WithdrawalsPresenter(WithdrawalsContact.View interfaceView) {
        super(interfaceView);
    }

    public void withdrawals(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().withdrawals(gson.toJson(view.withdrawalsParam()),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.withdrawalsFailed(e.getMessage());
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
                    view.withdrawalsSuccess(model.getMsg());
                }else {
                    view.withdrawalsFailed(model.getMsg());
                }
            }
        });
    }
}