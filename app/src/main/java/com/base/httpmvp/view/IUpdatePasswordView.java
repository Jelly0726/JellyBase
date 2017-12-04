package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IUpdatePasswordView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getUpdatePasswordParam();

    /**
     * 成功回调
     */
    public void updatePasswordSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void updatePasswordFailed(boolean isRefresh, String message);


}