package com.base.httpmvp.contact;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface UpdataPwdContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getUpdatePasswordParam();

        /**
         * 成功回调
         */
        public void updatePasswordSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void updatePasswordFailed(String message);


    }
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void updatePassword();
    }
}
