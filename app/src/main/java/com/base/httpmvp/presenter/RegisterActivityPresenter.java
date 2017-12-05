package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.RegisterContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter extends BasePresenterImpl<RegisterContact.View>
implements RegisterContact.Presenter{


    public RegisterActivityPresenter(RegisterContact.View interfaceView) {
        super(interfaceView);
    }

    public void userRegister(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.register(view.getRegParam(), composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResult = (HttpResult)mCallBackVo;
                if (httpResult.getStatus()== HttpCode.SUCCEED){
                    view.excuteSuccess(isRefresh,mCallBackVo);
                }else {
                    view.excuteFailed(isRefresh, httpResult.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.excuteFailed(isRefresh,message);
            }
        });
    }
    public void getVerifiCode(final boolean isRefresh,ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.getVerifiCode(view.getVerifiCodeParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    view.verifiCodeSuccess(isRefresh,mCallBackVo);
                }else {
                    view.verifiCodeFailed(isRefresh,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.verifiCodeFailed(isRefresh,message);
            }
        });
    }
}