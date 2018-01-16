package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

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
    public interface Presenter extends IBasePresenter {
        public void updatePhone(ObservableTransformer composer);
        public void getVerifiCode(ObservableTransformer composer);
    }
}
