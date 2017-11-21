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
    public void uploadSuccess(boolean isRefresh, Object mCallBackVo);

    /**
     * 失败回调
     */
    public void uploadFailed(boolean isRefresh, String message);


}