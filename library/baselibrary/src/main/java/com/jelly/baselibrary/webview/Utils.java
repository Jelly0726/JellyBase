package com.jelly.baselibrary.webview;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import com.jelly.baselibrary.log.LogUtils;
import com.tencent.smtt.sdk.CacheManager;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

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

    /**
     * 使用系统的下载服务
     DownloadManager 是系统提供的用于处理下载的服务，使用者只需提供下载 URI 和存储路径，并进行简单的设置。
     DownloadManager 会在后台进行下载，并且在下载失败、网络切换以及系统重启后尝试重新下载。
     * @param url                       下载地址
     * @param contentDisposition
     * @param mimeType
     */
    public static void downloadBySystem(Context context,String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimeType);
        LogUtils.i("fileName:{}", fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) context.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        LogUtils.i("downloadId:{}", downloadId+"");
    }
}
