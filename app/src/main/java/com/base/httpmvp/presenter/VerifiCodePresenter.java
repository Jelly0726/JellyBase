package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IVerifiCodeView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取验证码View(activityview)对应的Presenter
 */
public class VerifiCodePresenter implements IBasePresenter {

    private IVerifiCodeView interfaceView;

    public VerifiCodePresenter(IVerifiCodeView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void getVerifiCode(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.getVerifiCode(interfaceView.getVerifiCodeParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.verifiCodeSuccess(true,mCallBackVo);
                }else {
                    interfaceView.verifiCodeFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.verifiCodeFailed(true,message);
            }
        });
    }
}