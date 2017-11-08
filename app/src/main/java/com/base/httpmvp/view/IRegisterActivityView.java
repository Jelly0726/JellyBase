package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IRegisterActivityView {

    /**
     * 获取参数
     *
     * @return
     */
    public Object getParamenters();

    /**
     * 显示操作进度
     */
    public void showProgress();

    /**
     * 关闭进度
     */
    public void closeProgress();

    /**
     * 成功回调
     */
    public void excuteSuccessCallBack(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void excuteFailedCallBack(boolean isRefresh, String message);


}