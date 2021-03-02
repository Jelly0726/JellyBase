package com.base.httpmvp.mvpContact;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

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
    public abstract class Presenter extends BasePresenter<View, BaseModel> {
        public abstract void getMessage(final boolean isRefresh);
    }
}
