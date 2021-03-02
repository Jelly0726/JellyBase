package com.base.httpmvp.mvpPresenter;

import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.mvpContact.AccountDetailContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.AccountDetail;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：账户明细View(activityview)对应的Presenter
 */
public class AccountDetailPresenter extends AccountDetailContact.Presenter{
    public void accountDetail(final boolean isRefresh) {
        //view.showProgress();
        Observable observable =  HttpMethods.getInstance().getProxy(IApiService.class)
                .accountDetails(GlobalToken.getToken().getToken(),mGson.toJson(mView.getAccountDetailParam()))
                .flatMap(new HttpFunctions<HttpResultList<AccountDetail>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultList<AccountDetail>>() {
            @Override
            public void onSuccess(HttpResultList<AccountDetail> model) {
                mView.accountDetailSuccess(isRefresh,model.getData());
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.accountDetailFailed(isRefresh,msg);
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(),disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel=new BaseModel();
    }
}