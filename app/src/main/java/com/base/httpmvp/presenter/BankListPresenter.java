package com.base.httpmvp.presenter;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.contact.BankCartListContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultList;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取银行卡列表View(activityview)对应的Presenter
 */
public class BankListPresenter extends BasePresenterImpl<BankCartListContact.View>
        implements BankCartListContact.Presenter {


    public BankListPresenter(BankCartListContact.View interfaceView) {
        super(interfaceView);
    }

    public void bankList(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().bankList(gson.toJson(view.getBankListParam()),composer,new Observer<HttpResultList<BankCardInfo>>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.bankListFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultList<BankCardInfo> model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.bankListSuccess(isRefresh,model.getData());
                }else {
                    view.bankListFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}