package com.base.httpmvp.retrofitapi.util;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 注意点
 1.对Response的缓存策略进行修改的拦截器一定要应用于网络拦截器，否则无法缓存数据，因为在Response返回的过程中，
   普通的拦截器在内置的CacheInterceptor之后执行；
 2.修改Response的Cache-Control时，max-Age不能太大，否则你将在指定的max-Age时间内访问的始终是缓存数据（即便是有网的情况下）；
 3.实际的开发过程中，我们在网络请求中会添加一些公共参数，对于一些可变的公共参数，在缓存数据和访问缓存数据的过程中需要删除，
   比如网络类型，有网络时其值为Wifi或4G等，无网络时可能为none, 这时访问缓存时就会因url不一致导致访问缓存失败。
 */
public class HttpCacheInterceptor implements Interceptor {

    private Context context;

    public HttpCacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        //int maxAge = 1 * 60; // 在线缓存在1分钟内可读取 单位:秒
        int maxAge = 1; // 在线缓存在1秒内可读取 单位:秒
        return originalResponse.newBuilder()
                .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();
    }
}