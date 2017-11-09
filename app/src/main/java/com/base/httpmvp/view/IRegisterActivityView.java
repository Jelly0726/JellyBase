package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IRegisterActivityView extends IBaseView{
    /**
     * 获取参数
     *
     * @return
     */
    public Object getParamenters();
    /**
     * 成功回调
     */
    public void excuteSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void excuteFailed(boolean isRefresh, String message);


}