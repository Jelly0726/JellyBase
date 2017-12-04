package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IVerifiCodeView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getVerifiCodeParam();

    /**
     * 成功回调
     */
    public void verifiCodeSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void verifiCodeFailed(boolean isRefresh, String message);


}