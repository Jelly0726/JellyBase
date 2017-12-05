package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface AboutContact {
    public interface View extends IBaseView {
        /**
         * 成功回调
         */
        public void aboutUsSuccess(boolean isRefresh, Object mCallBackVo);

        /**
         * 失败回调
         */
        public void aboutUsFailed(boolean isRefresh, String message);


    }
    public interface Presenter extends IBasePresenter {
        public void aboutUs(final boolean isRefresh, ObservableTransformer composer);
    }
}
