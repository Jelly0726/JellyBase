package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultList;
import com.base.httpmvp.view.IBankListView;

import java.util.List;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取银行卡列表View(activityview)对应的Presenter
 */
public class BankListPresenter implements IBasePresenter {

    private IBankListView interfaceView;

    public BankListPresenter(IBankListView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void bankList(final boolean isRefresh, ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.bankList(interfaceView.getBankListParam(),composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResultList httpResultAll= (HttpResultList)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    List model= (List) httpResultAll.getData();
                    interfaceView.bankListSuccess(isRefresh,model);
                }else {
                    interfaceView.bankListFailed(isRefresh,httpResultAll.getMsg());
                }
            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.bankListFailed(isRefresh,message);
            }
        });
    }
}