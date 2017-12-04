package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.mode.databean.AboutUs;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IAboutUsView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：关于我们View(activityview)对应的Presenter
 */
public class AboutUsPresenter implements IBasePresenter {

    private IAboutUsView interfaceView;

    public AboutUsPresenter(IAboutUsView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void aboutUs(final boolean isRefresh, ObservableTransformer composer) {
        interfaceView.showProgress();
        mIBusiness.aboutUs(composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResultData httpResultAll= (HttpResultData)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    AboutUs model= (AboutUs) httpResultAll.getData();
                    interfaceView.aboutUsSuccess(isRefresh,model);
                }else {
                    interfaceView.aboutUsFailed(isRefresh,httpResultAll.getMsg());
                }
            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.aboutUsFailed(isRefresh,message);
            }
        });
    }
}