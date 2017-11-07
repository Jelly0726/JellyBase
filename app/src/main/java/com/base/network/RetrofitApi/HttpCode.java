package com.base.network.RetrofitApi;

/**
 * Created by david on 16/8/21.
 * 请求结果状态码
 */
public class HttpCode {
    /**
     * 请求成功
     */
    public static final int SUCCEED = 1;
    /**
     * token不存在
     */
    public static final int TOKEN_NOT_EXIST = 1000;
    /**
     * token无效
     */
    public static final int TOKEN_INVALID = 1001;
}