package com.jelly.mvp.contact;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/12/14.
 */

public interface SettingContact {
    public interface View extends IBaseView {
        /**
         * 成功回调
         */
        public void getAppversionSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void getAppversionFailed(String message);


    }
    public abstract class Presenter extends BasePresenter<View, BaseModel> {
        public abstract void getAppversion();
    }
}
