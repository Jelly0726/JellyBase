package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.BasePresenter;
import com.base.httpmvp.view.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface RegisterContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getRegParam();

        /**
         * 成功回调
         */
        public void excuteSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void excuteFailed(String message);

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
        public abstract void userRegister();
        public abstract void getVerifiCode();
    }
}
