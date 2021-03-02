package com.base.httpmvp.mvpContact;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface UpdatePhoneContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getUpdatePhoneParam();

        /**
         * 成功回调
         */
        public void updatePhoneSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void updatePhoneFailed(String message);

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
        public abstract void updatePhone();
        public abstract void getVerifiCode();
    }
}
