package com.base.httpmvp.view;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IUploadView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getUpParam();

    /**
     * 成功回调
     */
    public void uploadSuccessCallBack(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void uploadFailedCallBack(boolean isRefresh, String message);


}