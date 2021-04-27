package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.model.AccountDetail;
import com.jelly.mvp.contact.AccountDetailContact;

import io.reactivex.Observable;

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
            }
            @Override
            public void onFailure(String msg) {
                mView.accountDetailFailed(isRefresh,msg);
            }
        });
    }

    @Override
    public void start() {
        mModel=new BaseModel();
    }
}