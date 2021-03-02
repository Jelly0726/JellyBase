package com.base.httpmvp.mvpPresenter;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.mvpModel.BankCartModel;
import com.base.httpmvp.mvpView.IGetBankView;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取所属银行View(activityview)对应的Presenter
 */
public class GetBankPresenter extends BasePresenter<IGetBankView, BankCartModel> {

    public void getBank() {
        mView.showProgress();
        Observable observable =  HttpMethods.getInstance().getProxy(IApiService.class)
                .getBank(GlobalToken.getToken().getToken(),mGson.toJson(mView.getBankParam()))
                .flatMap(new HttpFunctions<HttpResultData<BankCardInfo>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultData<BankCardInfo>>() {
            @Override
            public void onSuccess(HttpResultData<BankCardInfo> model) {
                mView.getBankSuccess(model.getData());
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.getBankFailed(msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel = new BankCartModel();
    }
}