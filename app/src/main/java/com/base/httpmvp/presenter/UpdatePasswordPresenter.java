package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.UpdataPwdContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改密码View(activityview)对应的Presenter
 */
public class UpdatePasswordPresenter extends BasePresenterImpl<UpdataPwdContact.View>
        implements UpdataPwdContact.Presenter {


    public UpdatePasswordPresenter(UpdataPwdContact.View interfaceView) {
        super(interfaceView);
    }

    public void updatePassword(final boolean isRefresh,ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.updatePassword(view.getUpdatePasswordParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    view.updatePasswordSuccess(isRefresh,httpResultAll.getMsg());
                }else {
                    view.updatePasswordFailed(isRefresh,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.updatePasswordFailed(isRefresh,message);
            }
        });
    }
}