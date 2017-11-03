package com.base.okGo;

/**
 * Created by Administrator on 2017/6/14.
 */

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 这里只是我随便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
 */
public class SafeHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        //验证主机名是否匹配
        //return hostname.equals("server.jeasonlzy.com");
        return true;
    }
}