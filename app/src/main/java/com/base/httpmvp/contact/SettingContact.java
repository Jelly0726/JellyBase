package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/14.
 */

public interface SettingContact {
    public interface View extends IBaseView {
        /**
         * 成功回调
         */
        public void getAppversionSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void getAppversionFailed(String message);


    }
    public interface Presenter extends IBasePresenter {
        public void getAppversion(ObservableTransformer composer);
    }
}