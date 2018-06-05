package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface MessageDetailsContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getMessageDetailsParam();
        /**
         * 成功回调
         */
        public void getMessageDetailsSuccess( Object mCallBackVo);

        /**
         * 失败回调
         */
        public void getMessageDetailsFailed(String message);


    }
    public interface Presenter extends IBasePresenter {
        public void getMessageDetails(ObservableTransformer composer);
    }
}
