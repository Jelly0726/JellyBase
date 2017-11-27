package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.Business;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IRegisterActivityView;
import com.iflytek.cloud.thirdparty.T;

import java.util.List;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter implements IBasePresenter {

    private IRegisterActivityView interfaceView;
    private Business mIBusiness=new Business();

    public RegisterActivityPresenter(IRegisterActivityView interfaceView) {
        this.interfaceView = interfaceView;
    }

    /**
     * 注册
     * @param isRefresh 是否刷新 true 刷新 false加载
     */
    public void userRegister(final boolean isRefresh,ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.register(interfaceView.getParamenters(),composer, new ICallBackListener<T>() {
            @Override
            public void onSuccess(final T mCallBackVo) {
                HttpResult httpResult = (HttpResult)((List)mCallBackVo).get(0);
                if (httpResult.getStatus()== HttpCode.SUCCEED){
                    interfaceView.excuteSuccess(isRefresh,mCallBackVo);
                }else {
                    interfaceView.excuteFailed(isRefresh, httpResult.getMsg());
                }
            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.excuteFailed(isRefresh,message);
            }
        });
    }
}