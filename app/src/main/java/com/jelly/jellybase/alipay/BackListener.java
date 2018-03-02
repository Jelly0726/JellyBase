package com.jelly.jellybase.alipay;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 登录回调监听
 * Created by Administrator on 2015/12/28.
 */
public interface BackListener {
    public Object parseNetworkResponse(Response response, int id, String s);

    public void onError(Call call, Exception e, int id);

    public void onResponse(Object response, int id);
}
