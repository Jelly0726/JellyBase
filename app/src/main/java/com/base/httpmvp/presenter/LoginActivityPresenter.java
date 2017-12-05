package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.LoginContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;

import io.reactivex.ObservableTransformer;
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
        mIBusiness.login(view.getLoginParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResultData<Login> httpResultAll= (HttpResultData<Login>)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    Login model=httpResultAll.getData();
                    view.loginSuccess(isRefresh,model);
                }else {
                    view.loginFailed(isRefresh,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.loginFailed(isRefresh,message);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void addDisposable(Disposable subscription) {

    }

    @Override
    public void unDisposable() {

    }
}