package com.base.httpmvp.contact;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface BankCartContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object deletebankParam();

        /**
         * 成功回调
         */
        public void deletebankSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void deletebankFailed(String message);


    }
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void deletebank();
    }
}
