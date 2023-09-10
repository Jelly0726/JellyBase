package net.arvin.socialhelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by arvinljw on 17/11/27 15:37
 * Function：
 * Desc：
 */
final class SocialUtil {

    static String get(URL url) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while (-1 != (len = is.read(buffer))) {
                out.write(buffer, 0, len);
                out.flush();
            }
            return out.toString("utf-8");
        }
        return null;
    }

    static String getAppStateName(Context context) {
        String packageName = context.getPackageName();
        int beginIndex = 0;
        if (packageName.contains(".")) {
            beginIndex = packageName.lastIndexOf(".");
        }
        return packageName.substring(beginIndex);
    }

    static String getQQSex(String gender) {
        return "男".equals(gender) ? "M" : "F";
    }

    static String getWXSex(String gender) {
        return "1".equals(gender) ? "M" : "F";
    }

    static String getWBSex(String gender) {
        return "m".equals(gender) ? "M" : "F";
    }

    static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    static byte[] bmpToByteArray(final Bitmap bmp, boolean needThumb) {
        Bitmap newBmp;
        if (needThumb) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            if (width > height) {
                height = height * 150 / width;
                width = 150;
            } else {
                width = width * 150 / height;
                height = 150;
            }
            newBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
        } else {
            newBmp = bmp;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        newBmp.compress(Bitmap.CompressFormat.JPEG, 100, output);

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!bmp.isRecycled()) {
                bmp.recycle();
            }
            if (!newBmp.isRecycled()) {
                newBmp.recycle();
            }
        }

        return result;
    }

    /**
     * 是否安装qq
     */
    static boolean isQQInstalled(Context context) {
        return isAppInstall(context,"com.tencent.mobileqq");
    }
    /**
     *  是否安装新浪微博
     * @param context
     * @return
     */
    static boolean isWbInstall(@NonNull Context context) {
        return isAppInstall(context,"com.sina.weibo");
    }

    /**
     * 是否安装app
     * @param context
     * @param packageName
     * @return
     */
    static boolean isAppInstall(@NonNull Context context,@NonNull String packageName){
        PackageManager pm;
        if ((pm = context.getApplicationContext().getPackageManager()) == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if (packageName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
