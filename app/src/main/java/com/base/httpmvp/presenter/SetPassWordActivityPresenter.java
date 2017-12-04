package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.ISetPassWordActivityView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：设置密码View(activityview)对应的Presenter
 */
public class SetPassWordActivityPresenter implements IBasePresenter {

    private ISetPassWordActivityView interfaceView;

    public SetPassWordActivityPresenter(ISetPassWordActivityView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void setPassword(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.setPassword(interfaceView.getSetPassWordParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResult = (HttpResult)mCallBackVo;
                if (httpResult.getStatus()== HttpCode.SUCCEED){
                    interfaceView.excuteSuccess(true,httpResult.getMsg());
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