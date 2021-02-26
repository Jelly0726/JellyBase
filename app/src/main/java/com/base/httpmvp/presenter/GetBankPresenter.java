package com.base.httpmvp.presenter;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.view.IGetBankView;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取所属银行View(activityview)对应的Presenter
 */
public class GetBankPresenter extends BasePresenter<IGetBankView> {

    public void getBank() {
        mView.showProgress();
        HttpMethods.getInstance().getBank(mGson.toJson(mView.getBankParam()), mView.bindLifecycle(),new Observer<HttpResultData<BankCardInfo>>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.getBankFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<BankCardInfo> model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.getBankSuccess(model.getData());
                }else {
                    mView.getBankFailed(model.getMsg());
                }
            }
        });
    }
}