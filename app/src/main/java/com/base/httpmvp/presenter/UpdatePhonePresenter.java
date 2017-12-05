package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.UpdatePhoneContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改手机号View(activityview)对应的Presenter
 */
public class UpdatePhonePresenter extends BasePresenterImpl<UpdatePhoneContact.View>
implements UpdatePhoneContact.Presenter{


    public UpdatePhonePresenter(UpdatePhoneContact.View interfaceView) {
        super(interfaceView);
    }

    public void updatePhone(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.updatePhone(view.getUpdatePhoneParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    view.updatePhoneSuccess(isRefresh,httpResultAll.getMsg());
                }else {
                    view.updatePhoneFailed(isRefresh,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.updatePhoneFailed(isRefresh,message);
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