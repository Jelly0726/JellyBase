package com.base.httpmvp.contact;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

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
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void aboutUs(final boolean isRefresh);
    }
}
