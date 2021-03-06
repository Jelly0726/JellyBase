package com.jelly.mvp.contact;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;
import com.jelly.mvp.model.BankCartModel;

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
    public abstract class Presenter extends BasePresenter<View, BankCartModel> {
        public abstract void bankList(final boolean isRefresh);
    }
}
