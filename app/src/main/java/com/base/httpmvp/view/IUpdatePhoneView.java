package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IUpdatePhoneView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getUpdatePhoneParam();

    /**
     * 成功回调
     */
    public void updatePhoneSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void updatePhoneFailed(boolean isRefresh, String message);


}