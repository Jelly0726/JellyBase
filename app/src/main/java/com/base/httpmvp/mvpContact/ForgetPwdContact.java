package com.base.httpmvp.mvpContact;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

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
    public abstract class Presenter extends BasePresenter<View, BaseModel> {
        public abstract void forgetPwd();
        public abstract void getVerifiCode();
    }
}
