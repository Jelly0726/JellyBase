package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.BasePresenter;
import com.base.httpmvp.view.IBaseView;

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
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void operaAddress();
    }
}
