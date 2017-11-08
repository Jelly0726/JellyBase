package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.Business;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.view.IRegisterActivityView;
import com.iflytek.cloud.thirdparty.T;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter implements IBasePresenter {

    private IRegisterActivityView mIRegisterActivityView;
    private Business mIBusiness=new Business();

    public RegisterActivityPresenter(IRegisterActivityView mIRegisterActivityView) {
        this.mIRegisterActivityView = mIRegisterActivityView;
    }

    /**
     * 注册
     * @param isRefresh 是否刷新 true 刷新 false加载
     */
    public void userRegister(final boolean isRefresh) {
        mIRegisterActivityView.showProgress();
        mIBusiness.register(mIRegisterActivityView.getParamenters(), new ICallBackListener<T>() {
            @Override
            public void onSuccess(final T mCallBackVo) {
                mIRegisterActivityView.closeProgress();
                mIRegisterActivityView.excuteSuccessCallBack(isRefresh,mCallBackVo);
            }

            @Override
            public void onFaild(final String message) {
                mIRegisterActivityView.closeProgress();
                mIRegisterActivityView.excuteFailedCallBack(isRefresh,message);
            }
        });
    }
}