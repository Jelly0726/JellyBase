package com.previewlibrary.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.util.Log;

import com.previewlibrary.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author yangc
 *         date 2017/11/22
 *         E-Mail:yangchaojiang@outlook.com
 *         Deprecated:
 */

public class ImageUtils {
    /**
     * 取状态栏高度
     *
     * @param mApplicationContent mApplicationContent
     * @return int
     */
    public static int getStatusBarHeight(Context mApplicationContent) {
        int result = mApplicationContent.getResources().getDimensionPixelSize(R.dimen.yms_dimens_50_0_px);
        int resourceId = mApplicationContent.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mApplicationContent.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isLongPressed(float lastX, float lastY, float thisX, float thisY, long lastDownTime, long thisEventTime,long longPressTime) {

        float offsetX = Math.abs(thisX - lastX);

        float offsetY = Math.abs(thisY - lastY);

        long intervalTime = thisEventTime - lastDownTime;

        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {

            return true;

        }

        return false;

    }
    /**
     * 使用浏览器打开PDF：（APP外部打开，适用于加载网络PDF）
     * @param context
     * @param url
     */
    public static void openPDFInBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("error", "Activity was not found for intent, " + intent.toString());
        }
    }
    /**
     * 根据URL得到文件名称
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        String filename = "";
        boolean isok = false;
        // 从UrlConnection中获取文件名称
        try {
            URL myURL = new URL(url);

            URLConnection conn = myURL.openConnection();
            if (conn == null) {
                return null;
            }
            Map<String, List<String>> hf = conn.getHeaderFields();
            if (hf == null) {
                return null;
            }
            Set<String> key = hf.keySet();
            if (key == null) {
                return null;
            }
            // Log.i("test", "getContentType:" + conn.getContentType() + ",Url:"
            // + conn.getURL().toString());
            for (String skey : key) {
                List<String> values = hf.get(skey);
                for (String value : values) {
                    String result;
                    try {
                        result = new String(value.getBytes("ISO-8859-1"), "GBK");
                        int location = result.indexOf("filename");
                        if (location >= 0) {
                            result = result.substring(location
                                    + "filename".length());
                            filename = result
                                    .substring(result.indexOf("=") + 1);
                            isok = true;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }// ISO-8859-1 UTF-8 gb2312
                }
                if (isok) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 从路径中获取
        if (filename == null || "".equals(filename)) {
            filename = url.substring(url.lastIndexOf("/") + 1);
        }
        return filename;
    }
}
