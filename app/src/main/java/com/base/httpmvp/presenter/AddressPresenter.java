package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AddressContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.retrofitapi.HttpResultList;
import com.jelly.jellybase.datamodel.RecevierAddress;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：收货地址View(activityview)对应的Presenter
 */
public class AddressPresenter extends BasePresenterImpl<AddressContact.View> implements AddressContact.Presenter {


    public AddressPresenter(AddressContact.View interfaceView) {
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
    @Override
    public void getAddressList(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().getAddressList(gson.toJson(view.getAddressListParam()),composer
                ,new Observer<HttpResultList<RecevierAddress>>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.getAddressListFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultList<RecevierAddress> model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.getAddressListSuccess(isRefresh,model.getData());
                }else {
                    view.getAddressListFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}