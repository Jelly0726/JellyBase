package com.base.Utils;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 获取设备总存储器信息
 */
public class StorageQueryUtil {
    /**
     * 获取设备总存储器信息
     * @param context
     * @return long[0] 总大小 long[1] 已用大小 long[2]可用大小
     */
    public static long[] queryWithStorageManager(Context context) {
        long[] storage=new long[3];
        storage[0]=0l;//总大小
        storage[1]=0l;//已用大小
        storage[2]=0l;//可用大小
        //5.0 查外置存储
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        float unit = 1024, unit2 = 1000;
        int version = Build.VERSION.SDK_INT;
        if (version < Build.VERSION_CODES.M) {//小于6.0
            try {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                long totalSize = 0, availableSize = 0;
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        totalSize += file.getTotalSpace();
                        availableSize += file.getUsableSpace();
                    }
                }
//                Log.d(TAG, "totalSize = " + getUnit(totalSize, unit) + " ,availableSize = " + getUnit(availableSize, unit));
                storage[0]=totalSize;//总大小
                storage[1]=totalSize-availableSize;//已用大小
                storage[2]=availableSize;//可用大小
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {

            try {
                Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");//6.0
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                long total = 0L, used = 0L, systemSize = 0L;
                for (Object obj : getVolumeInfo) {

                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
//                    Log.d(TAG, "type: " + type);
                    if (type == 1) {//TYPE_PRIVATE

                        long totalSize = 0L;

                        //获取内置内存总大小
                        if (version >= Build.VERSION_CODES.O) {//8.0
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(context, fsUuid);//8.0 以后使用
                        } else if (version >= Build.VERSION_CODES.N_MR1) {//7.1.1
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");//5.0 6.0 7.0没有
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
                        }

                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);

                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            systemSize = totalSize - f.getTotalSpace();
                            used += totalSize - f.getFreeSpace();
                            total += totalSize;
                        }
//                        Log.d(TAG, "type = " + type + "totalSize = " + getUnit(totalSize, unit)
//                                + " ,used(with system) = " + getUnit(used, unit)
//                                + " ,free = " + getUnit(totalSize - used, unit));
                    } else if (type == 0) {//TYPE_PUBLIC
                        //外置存储
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            used += f.getTotalSpace() - f.getFreeSpace();
                            total += f.getTotalSpace();
                        }
                    } else if (type == 2) {//TYPE_EMULATED

                    }
                }
//                Log.d(TAG, "总内存 total = " + getUnit(total, unit) + "\n已用 used(with system) = " + getUnit(used, unit)
//                        + "可用 available = " + getUnit(total - used, unit) + "系统大小：" + getUnit(systemSize, unit));
//
//                Log.d(TAG, "总内存 total = " + getUnit(total, unit2) + "\n已用 used(with system) = " + getUnit(used, 1000)
//                        + "可用 available = " + getUnit(total - used, unit2) + "系统大小：" + getUnit(systemSize, unit2));
                storage[0]=total;//总大小
                storage[1]=used;//已用大小
                storage[2]=total - used;//可用大小
            } catch (SecurityException e) {
//                Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS");
            } catch (Exception e) {
                e.printStackTrace();
                storage=queryWithStatFs();
            }
        }
        return storage;
    }

    private static long[] queryWithStatFs() {
        long[] storage=new long[3];
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        //存储块
        long blockCount = statFs.getBlockCount();
        //块大小
        long blockSize = statFs.getBlockSize();
        //可用块数量
        long availableCount = statFs.getAvailableBlocks();
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        long freeBlocks = statFs.getFreeBlocks();

        //level 18
//        long totalSize = statFs.getTotalBytes();
//        long availableSize = statFs.getAvailableBytes();

//        Log.d(TAG, "=========");
//        Log.d(TAG, "total = " + getUnit(blockSize * blockCount, 1024));
//        Log.d(TAG, "available = " + getUnit(blockSize * availableCount, 1024));
//        Log.d(TAG, "free = " + getUnit(blockSize * freeBlocks, 1024));
        storage[0]=blockSize * blockCount;//总大小
        storage[1]=blockSize *(blockCount-availableCount);//已用大小
        storage[0]=blockSize * availableCount;//可用大小
        return storage;
    }

    private static String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 进制转换
     */
    public static String getUnit(float size, float base) {
        int index = 0;
        while (size > base && index < 4) {
            size = size / base;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s ", size, units[index]);
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private static long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
