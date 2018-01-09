package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface OperaAddressContact {
    public interface View extends IBaseView {
        /**
         * 配置地址获取参数
         *
         * @return
         */
        public Object operaAddressParam();

        /**
         * 配置地址成功回调
         */
        public void operaAddressSuccess(Object mCallBackVo);

        /**
         * 配置地址失败回调
         */
        public void operaAddressFailed(String message);


    }
    public interface Presenter extends IBasePresenter {
        public void operaAddress(ObservableTransformer composer);
    }
}
