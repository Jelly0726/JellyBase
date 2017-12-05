package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

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
        public void updatePasswordSuccess(boolean isRefresh, Object mCallBackVo);

        /**
         * 失败回调
         */
        public void updatePasswordFailed(boolean isRefresh, String message);


    }
    public interface Presenter extends IBasePresenter {
        public void updatePassword(final boolean isRefresh, ObservableTransformer composer);
    }
}
