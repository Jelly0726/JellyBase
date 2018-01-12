package com.base.webview;

import android.net.Uri;

/**
 * Created by Administrator on 2018/1/12.
 */

public class Utils {
    /***
     * 获取url 指定name的value;
     * @param url
     * @param name
     * @return
     *
     * 项目有可能需要截取Url 链接中参数时，最好不要利用处理String的手段来做,可以方便地使用URI达到目的.
    步骤如下:
    1 将String类型的URL转变为URI
    2 利用URI的getQueryParameter方法获取参数

    例如在一个URL中需要获取appid和userId
    过程如下:
    Uri uri = Uri.parse(url);
    String appid= uri.getQueryParameter("appid");
    String userId= uri.getQueryParameter("userId");
     */
    public static String getValueByName(String url, String name) {
        String result = "";
//        int index = url.indexOf("?");
//        String temp = url.substring(index + 1);
//        String[] keyValue = temp.split("&");
//        for (String str : keyValue) {
//            if (str.contains(name)) {
//                result = str.replace(name + "=", "");
//                break;
//            }
//        }
        Uri uri = Uri.parse(url);
        result= uri.getQueryParameter(name);
        return result;
    }
}
