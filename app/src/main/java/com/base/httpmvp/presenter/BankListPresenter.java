package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.BankCartListContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultList;

import java.util.List;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：获取银行卡列表View(activityview)对应的Presenter
 */
public class BankListPresenter extends BasePresenterImpl<BankCartListContact.View>
        implements BankCartListContact.Presenter {


    public BankListPresenter(BankCartListContact.View interfaceView) {
        super(interfaceView);
    }

    public void bankList(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.bankList(view.getBankListParam(),composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResultList httpResultAll= (HttpResultList)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    List model= (List) httpResultAll.getData();
                    view.bankListSuccess(isRefresh,model);
                }else {
                    view.bankListFailed(isRefresh,httpResultAll.getMsg());
                }
            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.bankListFailed(isRefresh,message);
            }
        });
    }
}