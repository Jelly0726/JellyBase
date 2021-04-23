package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.mvp.contact.UpdataPwdContact;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改密码View(activityview)对应的Presenter
 */
public class UpdatePasswordPresenter extends UpdataPwdContact.Presenter {

    public void updatePassword() {
        mView.showProgress();
        Observable observable =  HttpMethods.getInstance().getProxy(IApiService.class)
                .updatePassword(GlobalToken.getToken().getToken()
                ,mGson.toJson(mView.getUpdatePasswordParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.updatePasswordSuccess(model.getMsg());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.updatePasswordFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}