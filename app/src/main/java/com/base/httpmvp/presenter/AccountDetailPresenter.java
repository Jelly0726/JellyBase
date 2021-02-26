package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AccountDetailContact;
import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.AccountDetail;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：账户明细View(activityview)对应的Presenter
 */
public class AccountDetailPresenter extends AccountDetailContact.Presenter{
    public void accountDetail(final boolean isRefresh) {
        //view.showProgress();
        Observer<HttpResultList<AccountDetail>> observer=new Observer<HttpResultList<AccountDetail>>() {

            @Override
            public void onError(Throwable e) {
                //view.closeProgress();
                mView.accountDetailFailed(isRefresh,e.getMessage());
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
                    mView.accountDetailSuccess(isRefresh,model.getData());
                }else {
                    mView.accountDetailFailed(isRefresh,model.getMsg());
                }
            }
        };
        Observable observable =  HttpMethods.getInstance().getProxy(IApiService.class)
                .accountDetails(GlobalToken.getToken().getToken(),mGson.toJson(mView.getAccountDetailParam()))
                .flatMap(new HttpFunctions<HttpResultList<AccountDetail>>());
        HttpMethods.getInstance().toSubscribe(observable,observer,mView.bindLifecycle());
    }
}