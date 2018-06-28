package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.CancelOrderContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：取消订单View(activityview)对应的Presenter
 */
public class CancelOrderPresenter extends BasePresenterImpl<CancelOrderContact.View>
implements CancelOrderContact.Presenter{


    public CancelOrderPresenter(CancelOrderContact.View interfaceView) {
        super(interfaceView);
    }

    public void cancelOrder(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().cancelOrder(gson.toJson(view.cancelOrderParam()),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.cancelOrderFailed(e.getMessage());
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
                    view.cancelOrderSuccess(model.getMsg());
                }else {
                    view.cancelOrderFailed(model.getMsg());
                }
            }
        });
    }
}