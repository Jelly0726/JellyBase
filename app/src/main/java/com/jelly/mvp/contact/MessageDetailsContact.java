package com.jelly.mvp.contact;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

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
    public abstract class Presenter extends BasePresenter<View, BaseModel> {
        public abstract void getMessageDetails();
    }
}
