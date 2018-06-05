package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface MessageContact {
    public interface View extends IBaseView {
        public Object getMessageParam();
        /**
         * 成功回调
         */
        public void getMessageSuccess(boolean isRefresh, Object mCallBackVo);

        /**
         * 失败回调
         */
        public void getMessageFailed(boolean isRefresh, String message);


    }
    public interface Presenter extends IBasePresenter {
        public void getMessage(final boolean isRefresh,ObservableTransformer composer);
    }
}
