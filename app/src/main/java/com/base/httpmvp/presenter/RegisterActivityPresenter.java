package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IRegisterActivityView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter implements IBasePresenter {

    private IRegisterActivityView interfaceView;

    public RegisterActivityPresenter(IRegisterActivityView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void userRegister( ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.register(interfaceView.getRegParam(), composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResult = (HttpResult)mCallBackVo;
                if (httpResult.getStatus()== HttpCode.SUCCEED){
                    interfaceView.excuteSuccess(true,mCallBackVo);
                }else {
                    interfaceView.excuteFailed(true, httpResult.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.excuteFailed(true,message);
            }
        });
    }
}