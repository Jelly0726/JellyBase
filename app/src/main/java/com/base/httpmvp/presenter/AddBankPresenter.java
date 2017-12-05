package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AddBankCartContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：添加银行卡View(activityview)对应的Presenter
 */
public class AddBankPresenter extends BasePresenterImpl<AddBankCartContact.View> implements AddBankCartContact.Presenter {


    public AddBankPresenter(AddBankCartContact.View interfaceView) {
        super(interfaceView);
    }

    @Override
    public void addBank(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.addbank(view.addBankParam(),composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    view.addBankSuccess(isRefresh,httpResultAll.getMsg());
                }else {
                    view.addBankFailed(isRefresh,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.addBankFailed(isRefresh,message);
            }
        });
    }
}