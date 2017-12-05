package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.LoginContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultData;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import systemdb.Login;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：登录View(activityview)对应的Presenter
 */
public class LoginActivityPresenter extends BasePresenterImpl<LoginContact.View>
implements LoginContact.Presenter{


    public LoginActivityPresenter(LoginContact.View interfaceView) {
        super(interfaceView);
    }

    public void userLogin(final boolean isRefresh,ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().userLogin(gson.toJson(view.getLoginParam()),composer,new Observer<HttpResultData<Login>>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.loginFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<Login> model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.loginSuccess(isRefresh,model.getData());
                }else {
                    view.loginFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}