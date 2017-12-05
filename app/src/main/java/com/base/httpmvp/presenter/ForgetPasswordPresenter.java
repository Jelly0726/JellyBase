package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.ForgetPwdContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：忘记密码View(activityview)对应的Presenter
 */
public class ForgetPasswordPresenter extends BasePresenterImpl<ForgetPwdContact.View>
implements ForgetPwdContact.Presenter{


    public ForgetPasswordPresenter(ForgetPwdContact.View interfaceView) {
        super(interfaceView);
    }

    public void forgetPwd(final boolean isRefresh,ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.forgetPwd(view.forgetPasswordParam(),composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    view.forgetPasswordSuccess(isRefresh,httpResultAll.getMsg());
                }else {
                    view.forgetPasswordFailed(isRefresh,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.forgetPasswordFailed(isRefresh,message);
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