package com.base.webview;

import android.net.Uri;

import com.tencent.smtt.sdk.CacheManager;

import java.io.File;

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

    /**
     * 删除在此之前的缓存
     * @param dir
     * @param numDays
     * @return
     */
    public static int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child,numDays);
                    }
                    if (child.lastModified()< numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 退出清除缓存
     */
    public static void exitClearCache(){
        File file = CacheManager.getCacheFileBaseDir();
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();}
            file.delete();}
    }
}
