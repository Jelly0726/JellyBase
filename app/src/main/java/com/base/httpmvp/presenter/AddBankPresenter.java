package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.IAddBankView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：添加银行卡View(activityview)对应的Presenter
 */
public class AddBankPresenter implements IBasePresenter {

    private IAddBankView interfaceView;

    public AddBankPresenter(IAddBankView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void addBank(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.addbank(interfaceView.addBankParam(),composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResult httpResultAll= (HttpResult)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.addBankSuccess(true,httpResultAll.getMsg());
                }else {
                    interfaceView.addBankFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.addBankFailed(true,message);
            }
        });
    }
}