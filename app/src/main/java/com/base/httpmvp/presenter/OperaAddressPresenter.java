package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.OperaAddressContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：配置收货地址View(activityview)对应的Presenter
 */
public class OperaAddressPresenter extends BasePresenterImpl<OperaAddressContact.View> implements OperaAddressContact.Presenter {


    public OperaAddressPresenter(OperaAddressContact.View interfaceView) {
        super(interfaceView);
    }

    @Override
    public void operaAddress(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().operaAddress(gson.toJson(view.operaAddressParam()),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.operaAddressFailed(e.getMessage());
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
                    view.operaAddressSuccess(model.getMsg());
                }else {
                    view.operaAddressFailed(model.getMsg());
                }
            }
        });
    }
}