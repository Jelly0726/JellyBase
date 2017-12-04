package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IUpdatePasswordView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改密码View(activityview)对应的Presenter
 */
public class UpdatePasswordPresenter implements IBasePresenter {

    private IUpdatePasswordView interfaceView;

    public UpdatePasswordPresenter(IUpdatePasswordView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void updatePassword(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.updatePassword(interfaceView.getUpdatePasswordParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.updatePasswordSuccess(true,httpResultAll.getMsg());
                }else {
                    interfaceView.updatePasswordFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.updatePasswordFailed(true,message);
            }
        });
    }
}