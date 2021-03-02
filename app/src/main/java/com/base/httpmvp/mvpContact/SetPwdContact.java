package com.base.httpmvp.mvpContact;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface SetPwdContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getSetPassWordParam();

        /**
         * 成功回调
         */
        public void excuteSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void excuteFailed( String message);
    }
    public abstract class Presenter extends BasePresenter<View, BaseModel> {
        public abstract void setPassword();
    }
}
