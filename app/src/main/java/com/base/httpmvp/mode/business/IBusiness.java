package com.base.httpmvp.mode.business;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IBusiness {

    /**
     * 注册
     * @param mUserVo:请求参数：封装为bean
     * @param mICallBackListener:回调接口
     */
    public void register(Object mUserVo, ICallBackListener mICallBackListener);

    /**
     * 登录
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void login(Object obj, ICallBackListener mICallBackListener);

    /**
     * 忘记密码
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void forgetPwd(Object obj, ICallBackListener mICallBackListener);

    /**
     * 意见反馈
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void feedBack(Object obj, ICallBackListener mICallBackListener);
    /**
     * 上传文件（图片）
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void upload(Object obj, ICallBackListener mICallBackListener);

    //  TODO MORE
}
