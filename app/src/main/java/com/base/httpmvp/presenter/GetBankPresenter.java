package com.base.httpmvp.presenter;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IGetBankView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取所属银行View(activityview)对应的Presenter
 */
public class GetBankPresenter implements IBasePresenter {

    private IGetBankView interfaceView;

    public GetBankPresenter(IGetBankView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void getBank(ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.getBank(interfaceView.getBankParam(),composer, new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResultData<BankCardInfo> httpResultAll= (HttpResultData<BankCardInfo>)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.getBankSuccess(true,httpResultAll.getData());
                }else {
                    interfaceView.getBankFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.getBankFailed(true,message);
            }
        });
    }
}