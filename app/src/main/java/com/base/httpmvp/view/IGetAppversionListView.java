package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IGetAppversionListView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getAppversionListParam();

    /**
     * 成功回调
     */
    public void getAppversionListSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void getAppversionListFailed(boolean isRefresh, String message);


}