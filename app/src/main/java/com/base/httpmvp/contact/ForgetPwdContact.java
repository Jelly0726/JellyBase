package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface ForgetPwdContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object forgetPasswordParam();

        /**
         * 成功回调
         */
        public void forgetPasswordSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void forgetPasswordFailed(String message);

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
    public interface Presenter extends IBasePresenter {
        public void forgetPwd(ObservableTransformer composer);
        public void getVerifiCode(ObservableTransformer composer);
    }
}
