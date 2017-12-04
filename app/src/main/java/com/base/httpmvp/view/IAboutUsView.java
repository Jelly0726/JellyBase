package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IAboutUsView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getAboutUsParam();

    /**
     * 成功回调
     */
    public void aboutUsSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void aboutUsFailed(boolean isRefresh, String message);


}