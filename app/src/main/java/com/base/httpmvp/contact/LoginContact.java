package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.BasePresenter;
import com.base.httpmvp.view.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface LoginContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getLoginParam();

        /**
         * 成功回调
         */
        public void loginSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void loginFailed(String message);
        /**
         * 获取参数
         *
         * @return
         */
        public Object getVerifiCodeParam();

        /**
         * 成功回调
         */
        public void verifiCodeSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void verifiCodeFailed(String message);

    }
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void userLogin();
        public abstract void getVerifiCode();
    }
}
