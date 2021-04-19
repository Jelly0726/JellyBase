package com.base.httpmvp.retrofitapi.exception;


import android.net.ParseException;
import android.util.MalformedJsonException;

import com.jelly.baselibrary.log.LogUtils;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

/**
 * Created by liukun on 16/3/10.
 * 异常处理驱动
 */
public class ApiException extends RuntimeException {
    //对应HTTP的状态码
    //非法请求
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    //找不到
    private static final int NOT_FOUND = 404;
    //请求超时
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    //网关超时
    private static final int GATEWAY_TIMEOUT = 504;
    public ApiException(String detailMessage) {
        super(detailMessage);
    }
    public ApiException(Throwable throwable) {
        this(getApiExceptionMessage(throwable));
    }
    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param throwable
     * @return
     */
    private static String getApiExceptionMessage(Throwable throwable){
        String message = "";
        if (throwable instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) throwable;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    message = "网络错误("+httpException.code()+")";  //均视为网络错误
                    break;
            }
            return message;
        } else if (throwable instanceof JsonParseException
                || throwable instanceof JSONException
                || throwable instanceof ParseException
                || throwable instanceof MalformedJsonException) {
            message = "解析错误("+ ERROR.PARSE_ERROR+")";     //均视为解析错误
            return message;

        } else if (throwable instanceof ConnectException
                ||throwable instanceof SocketException
                ||throwable instanceof IOException) {
            message = "连接失败("+ ERROR.NETWORD_ERROR+")";  //均视为网络错误
            return message;
        } else if (throwable instanceof SocketTimeoutException) {//连接超时
            message = "连接超时(" + ERROR.NETWORD_ERROR + ")";//均视为网络错误
            return message;
        }else if (throwable instanceof TokenInvalidException){//token过期
            return "登录状态异常！";
        }else if (throwable instanceof TokenNotExistException) {//token不存在
            return "登录状态异常！";
        }else if (throwable instanceof ApiException) {//ApiException
            return throwable.getMessage();
        }else {
            message = "未知错误("+ERROR.UNKNOWN+"):"+throwable;          //未知错误
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw, true));
            LogUtils.i(sw.toString());
            return message;
        }
    }
    /**
     * 约定异常
     */

    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;
    }
}

