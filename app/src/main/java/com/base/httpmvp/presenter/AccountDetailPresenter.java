package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AccountDetailContact;
import com.base.model.AccountDetail;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：账户明细View(activityview)对应的Presenter
 */
public class AccountDetailPresenter extends BasePresenterImpl<AccountDetailContact.View>
implements AccountDetailContact.Presenter{


    public AccountDetailPresenter(AccountDetailContact.View interfaceView) {
        super(interfaceView);
    }

    public void accountDetail(final boolean isRefresh, ObservableTransformer composer) {
        //view.showProgress();
        HttpMethods.getInstance().accountDetails(gson.toJson(view.getAccountDetailParam()),composer,
                new Observer<HttpResultList<AccountDetail>>() {

            @Override
            public void onError(Throwable e) {
                //view.closeProgress();
                view.accountDetailFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultList<AccountDetail> model) {
               // view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.accountDetailSuccess(isRefresh,model.getData());
                }else {
                    view.accountDetailFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}