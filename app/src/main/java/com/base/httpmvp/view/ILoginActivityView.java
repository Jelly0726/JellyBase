package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface ILoginActivityView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getLoginParam();

    /**
     * 成功回调
     */
    public void loginSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void loginFailed(boolean isRefresh, String message);


}