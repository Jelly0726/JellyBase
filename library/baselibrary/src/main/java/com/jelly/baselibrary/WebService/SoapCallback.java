package com.jelly.baselibrary.WebService;
/**
 * 处理请求服务器的回调接口
 * Created by lipanquan on 2016/12/26.
 */
public class SoapCallback<ResponseResult> {
    /**
     * 请求失败处理的结果，Run main thread
     */
    public void onFailResult() {

    }

    /**
     * 请求成功处理的结果，Run main thread
     *
     * @param responseResult 请求成功处理后的结果
     */
    public void onResponseResult(ResponseResult responseResult) {

    }

    /**
     * onResponseResult 方法之后执行
     */
    public void onFinally() {

    }
}