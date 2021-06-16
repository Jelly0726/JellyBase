package com.base.utils;


import android.annotation.SuppressLint;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * TODO 获取APP应用  缓存大小 数据大小 应用大小
 */

public class AppSizeUtils {
    private static AppSizeUtils mApiUrl;
    private AppSizeUtils() {
    }
    public static AppSizeUtils getInstance() {
        if (mApiUrl == null) {
            synchronized (AppSizeUtils.class) {
                if (mApiUrl == null) {
                    mApiUrl = new AppSizeUtils();
                }
            }
        }
        return mApiUrl;
    }
    /**
     * TODO 获取应用总大小
     */
    public void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getAppSizeO(context);
        } else {
//            getAppsize(context);
        }
    }
    /**
     * 格式化单位
     * @param size
     * @return
     */
    public String getFormatSize(long size) {
        long kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        long megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        long gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        long teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
    /**
     * 获取应用的大小
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAppSizeO(Context context) {
        @SuppressLint("WrongConstant")
        StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        //获取所有应用的StorageVolume列表
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        for (StorageVolume item : storageVolumes) {
            String uuidStr = item.getUuid();
            UUID uuid;
            if (uuidStr == null) {
                uuid = StorageManager.UUID_DEFAULT;
            } else {
                uuid = UUID.fromString(uuidStr);
            }
            int uid = getUid(context, context.getPackageName());
            //通过包名获取uid
            StorageStats storageStats = null;
            try {
                storageStats = storageStatsManager.queryStatsForUid(uuid, uid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (onBackListent != null) {
                onBackListent.backData(storageStats.getCacheBytes(), storageStats.getDataBytes(), storageStats.getAppBytes());
            }

        }
    }

    /**
     * 根据应用包名获取对应uid
     */
    public int getUid(Context context, String pakName) {
        try {
            return context.getPackageManager().getApplicationInfo(pakName, PackageManager.GET_META_DATA).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取应用大小8.0以下
     */
//    public void getAppsize(Context context) {
//        try {
//            Method method = PackageManager.class.getMethod("getPackageSizeInfo",
//                    new Class[]{String.class, IPackageStatsObserver.class});
//            // 调用 getPackageSizeInfo 方法，需要两个参数：1、需要检测的应用包名；2、回调
//            method.invoke(context.getPackageManager(), context.getPackageName(),
//                    new IPackageStatsObserver.Stub() {
//                @Override
//                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
//                    //总缓存=应用内部缓存+外部缓存
//                    long cacheSize=pStats.cacheSize+pStats.externalCacheSize;
//                    //总数据=应用内部数据+外部数据+外部媒体+外部obb
//                    long dataSize=pStats.dataSize+pStats.externalDataSize+pStats.externalMediaSize+pStats.externalObbSize;
//                    //总程序大小=内部代码+外部代码
//                    long codeSize=pStats.codeSize+pStats.externalCodeSize;
//                    if (onBackListent != null) {
//                        onBackListent.backData(cacheSize,dataSize,codeSize);
//                    }
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public OnBackListent onBackListent;
    public interface OnBackListent {
        void backData(long cacheSize, long dataSize, long codeSize);
    }
    public AppSizeUtils setDatasListent(OnBackListent listent) {
        onBackListent = listent;
        return this;
    }
}
