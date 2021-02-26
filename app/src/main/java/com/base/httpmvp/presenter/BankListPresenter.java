package com.base.httpmvp.presenter;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.contact.BankCartListContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取银行卡列表View(activityview)对应的Presenter
 */
public class BankListPresenter extends BankCartListContact.Presenter {

    public void bankList(final boolean isRefresh) {
        mView.showProgress();
        HttpMethods.getInstance().bankList(mGson.toJson(mView.getBankListParam()), mView.bindLifecycle()
                ,new Observer<HttpResultList<BankCardInfo>>() {

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.bankListFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultList<BankCardInfo> model) {
                mView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    mView.bankListSuccess(isRefresh,model.getData());
                }else {
                    mView.bankListFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}