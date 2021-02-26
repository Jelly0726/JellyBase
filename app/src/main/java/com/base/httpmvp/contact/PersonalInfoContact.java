package com.base.httpmvp.contact;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/12/5.
 */

public interface PersonalInfoContact {
    public interface View extends IBaseView {
        /**
         * 获取参数
         *
         * @return
         */
        public Object getPersonalInfoParam();

        /**
         * 成功回调
         */
        public void personalInfoSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void personalInfoFailed(String message);

        /**
         * 成功回调
         */
        public void findPersonalInfoSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void findPersonalInfoFailed(String message);
        /**
         * 获取参数
         *
         * @return
         */
        public Object getUpParam();

        /**
         * 成功回调
         */
        public void uploadSuccess(Object mCallBackVo);

        /**
         * 失败回调
         */
        public void uploadFailed(String message);

    }
    public abstract class Presenter extends BasePresenter<View> {
        public abstract void getInfo();
        public abstract void upload();
        public abstract void upPersonalInfo();
    }
}
