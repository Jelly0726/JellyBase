package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.ILoginActivityView;

import io.reactivex.ObservableTransformer;
import systemdb.Login;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：登录View(activityview)对应的Presenter
 */
public class LoginActivityPresenter implements IBasePresenter {

    private ILoginActivityView interfaceView;

    public LoginActivityPresenter(ILoginActivityView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void userLogin(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.login(interfaceView.getLoginParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResultData<Login> httpResultAll= (HttpResultData<Login>)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    Login model=httpResultAll.getData();
                    interfaceView.loginSuccess(true,model);
                }else {
                    interfaceView.loginFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.loginFailed(true,message);
            }
        });
    }
}