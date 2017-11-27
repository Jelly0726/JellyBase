package com.base.httpmvp.mode.business;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IBusiness {
    /**
     * 获取token
     * @param mUserVo:请求参数：封装为bean
     */
    public void getToken(Object mUserVo,ObservableTransformer composer);
    /**
     * 注册
     * @param mUserVo:请求参数：封装为bean
     * @param mICallBackListener:回调接口
     */
    public void register(Object mUserVo,ObservableTransformer composer, ICallBackListener mICallBackListener);

    /**
     * 登录
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void login(Object obj,ObservableTransformer composer, ICallBackListener mICallBackListener);

    /**
     * 忘记密码
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void forgetPwd(Object obj,ObservableTransformer composer, ICallBackListener mICallBackListener);

    /**
     * 意见反馈
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void feedBack(Object obj,ObservableTransformer composer, ICallBackListener mICallBackListener);
    /**
     * 上传文件（图片）
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void upload(Object obj,ObservableTransformer composer, ICallBackListener mICallBackListener);

    //  TODO MORE
    /**
     * 获取所属银行
     * @param obj:请求参数，可根据实际需求定义
     * @param  mICallBackListener：回调接口
     */
    public void getBank(Object obj,ObservableTransformer composer, ICallBackListener mICallBackListener);
}
