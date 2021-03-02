package com.base.httpmvp.mvpView;

import com.base.httpmvp.mvpbase.IBaseView;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IGetBankView extends IBaseView {
    /**
     * 获取参数
     *
     * @return
     */
    public Object getBankParam();

    /**
     * 成功回调
     */
    public void getBankSuccess( Object mCallBackVo);

    /**
     * 失败回调
     */
    public void getBankFailed(String message);


}