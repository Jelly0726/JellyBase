package com.base.httpmvp.presenter;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IGetBankView;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取所属银行View(activityview)对应的Presenter
 */
public class GetBankPresenter implements IBasePresenter {

    private IGetBankView interfaceView;

    public GetBankPresenter(IGetBankView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void getBank(ObservableTransformer composer) {
        interfaceView.showProgress();
        HttpMethods.getInstance().getBank(gson.toJson(interfaceView.getBankParam()),composer,new Observer<HttpResultData<BankCardInfo>>() {

            @Override
            public void onError(Throwable e) {
                interfaceView.closeProgress();
                interfaceView.getBankFailed(true,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<BankCardInfo> model) {
                interfaceView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    interfaceView.getBankSuccess(true,model.getData());
                }else {
                    interfaceView.getBankFailed(true,model.getMsg());
                }
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void addDisposable(Disposable subscription) {

    }

    @Override
    public void unDisposable() {

    }
}