package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IAddBankView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object addBankParam();

    /**
     * 成功回调
     */
    public void addBankSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void addBankFailed(boolean isRefresh, String message);


}