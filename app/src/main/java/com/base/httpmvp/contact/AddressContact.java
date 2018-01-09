package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface AddressContact {
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
        /**
         * 获取地址获取参数
         *
         * @return
         */
        public Object getAddressListParam();
        /**
         * 获取地址成功回调
         */
        public void getAddressListSuccess(boolean isRefresh, Object mCallBackVo);

        /**
         * 获取地址失败回调
         */
        public void getAddressListFailed(boolean isRefresh, String message);


    }
    public interface Presenter extends IBasePresenter {
        public void operaAddress(ObservableTransformer composer);
        public void getAddressList(boolean isRefresh, ObservableTransformer composer);
    }
}