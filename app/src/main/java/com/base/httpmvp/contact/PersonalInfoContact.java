package com.base.httpmvp.contact;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.IBaseView;

import io.reactivex.ObservableTransformer;

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
    public interface Presenter extends IBasePresenter {
        public void getInfo(ObservableTransformer composer);
        public void upload(ObservableTransformer composer);
        public void upPersonalInfo(ObservableTransformer composer);
    }
}
