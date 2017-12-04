package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IBankListView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getBankListParam();

    /**
     * 成功回调
     */
    public void bankListSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void bankListFailed(boolean isRefresh, String message);


}