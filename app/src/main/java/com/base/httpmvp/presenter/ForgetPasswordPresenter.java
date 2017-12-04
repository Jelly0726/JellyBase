package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IForgetPasswordView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：忘记密码View(activityview)对应的Presenter
 */
public class ForgetPasswordPresenter implements IBasePresenter {

    private IForgetPasswordView interfaceView;

    public ForgetPasswordPresenter(IForgetPasswordView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void execute(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.forgetPwd(interfaceView.forgetPasswordParam(),composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.forgetPasswordSuccess(true,httpResultAll.getMsg());
                }else {
                    interfaceView.forgetPasswordFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.forgetPasswordFailed(true,message);
            }
        });
    }
}