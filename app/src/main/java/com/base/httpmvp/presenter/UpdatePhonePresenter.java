package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IUpdatePhoneView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改手机号View(activityview)对应的Presenter
 */
public class UpdatePhonePresenter implements IBasePresenter {

    private IUpdatePhoneView interfaceView;

    public UpdatePhonePresenter(IUpdatePhoneView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void updatePhone( ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.updatePhone(interfaceView.getUpdatePhoneParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.updatePhoneSuccess(true,httpResultAll.getMsg());
                }else {
                    interfaceView.updatePhoneFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.updatePhoneFailed(true,message);
            }
        });
    }
}