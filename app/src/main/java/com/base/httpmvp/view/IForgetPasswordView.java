package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IForgetPasswordView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object forgetPasswordParam();

    /**
     * 成功回调
     */
    public void forgetPasswordSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void forgetPasswordFailed(boolean isRefresh, String message);


}