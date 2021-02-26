package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.BasePresenter;
import com.base.httpmvp.view.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface BankCartListContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getBankListParam();

        /**
         * 成功回调
         */
        public void bankListSuccess(boolean isRefresh, Object mCallBackVo);

        /**
         * 失败回调
         */
        public void bankListFailed(boolean isRefresh, String message);


    }
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void bankList(final boolean isRefresh);
    }
}
