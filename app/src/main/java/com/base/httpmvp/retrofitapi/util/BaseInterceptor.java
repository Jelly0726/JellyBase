package com.base.httpmvp.retrofitapi.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.base.applicationUtil.AppUtils;
import com.base.httpmvp.retrofitapi.HttpUtils;
import com.base.httpmvp.retrofitapi.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
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
public class BaseInterceptor implements Interceptor {

    private Context mContext;

    public BaseInterceptor(Context context) {
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //网络不可用
        if (!NetworkUtils.isAvailable(mContext)) {
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                //在请求头中加入：强制使用缓存，不访问网络
                /*
					.addHeader("Connection", "close")
					解决java.io.IOException: unexpected end of stream on Connection
					主要就是在http header里面增加关闭连接，不让它保持连接。
					主要是在回收url connection有可能有问题，后来我也增加了连接关闭，
					不保持url connection，这样就解决了，但是付出了性能的代价。
				 */
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        //.addHeader("Connection", "close")
                        .addHeader("version", AppUtils.getVersionCode(mContext) + "")
                        .addHeader("Connection", "keep-alive")
                        .build();
            }else {
                //在请求头中加入：强制使用缓存，不访问网络
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .addHeader("version", AppUtils.getVersionCode(mContext) + "")
                        .addHeader("Connection", "keep-alive")
                        .build();
            }
            Log.i("sss","no network");
        }else {
            //请求头添加参数version
            request = request.newBuilder()
                    .addHeader("version", AppUtils.getVersionCode(mContext) + "")
                    .addHeader("Connection", "keep-alive")
                    .build();
        }
        Response response = chain.proceed(request);
        //网络可用
        if (NetworkUtils.isAvailable(mContext)) {
            int maxAge = 0;
            // 有网络时 在响应头中加入：设置缓存超时时间0个小时
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .build();
        } else {
            Log.i("sss","network error");
            // 无网络时，在响应头中加入：设置缓存超时为4周
            //离线缓存控制 总缓存时间=在线缓存时间+设置离线时的缓存时间
            int maxStale = 60 * 60 * 24 * 28;
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .build();
        }
        HttpUtils.checkResponse(response);
        return response;
    }

}