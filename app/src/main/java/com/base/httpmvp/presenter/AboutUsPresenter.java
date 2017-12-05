package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AboutContact;
import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.mode.databean.AboutUs;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：关于我们View(activityview)对应的Presenter
 */
public class AboutUsPresenter extends BasePresenterImpl<AboutContact.View> implements AboutContact.Presenter {


    public AboutUsPresenter(AboutContact.View interfaceView) {
        super(interfaceView);
    }

    public void aboutUs(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        mIBusiness.aboutUs(composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                view.closeProgress();
                HttpResultData httpResultAll= (HttpResultData)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    AboutUs model= (AboutUs) httpResultAll.getData();
                    view.aboutUsSuccess(isRefresh,model);
                }else {
                    view.aboutUsFailed(isRefresh,httpResultAll.getMsg());
                }
            }

            @Override
            public void onFaild(final String message) {
                view.closeProgress();
                view.aboutUsFailed(isRefresh,message);
            }
        });
    }
}