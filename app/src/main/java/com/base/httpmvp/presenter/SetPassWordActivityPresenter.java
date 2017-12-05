package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.SetPwdContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：设置密码View(activityview)对应的Presenter
 */
public class SetPassWordActivityPresenter extends BasePresenterImpl<SetPwdContact.View>
implements SetPwdContact.Presenter{


    public SetPassWordActivityPresenter(SetPwdContact.View interfaceView) {
        super(interfaceView);
    }

    @Override
    public void setPassword(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.setPassword(view.getSetPassWordParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResult = (HttpResult)mCallBackVo;
                if (httpResult.getStatus()== HttpCode.SUCCEED){
                    view.excuteSuccess(isRefresh,httpResult.getMsg());
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
}