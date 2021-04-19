package com.jelly.baselibrary.Utils;

import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 分析用户手机的cpu
 */
public class CPUUtils {
    @NonNull
    public static String getCpuName() {
        String name = getCpuName1();
        if (TextUtils.isEmpty(name)) {
            name = getCpuName2();
            if (TextUtils.isEmpty(name)) {
                name = "unknown";
            }
        }
        return name;
    }

    private static String getCpuName1() {
        String[] abiArr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abiArr = Build.SUPPORTED_ABIS;
        } else {
            abiArr = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }

        StringBuilder abiStr = new StringBuilder();
        for (String abi : abiArr) {
            abiStr.append(abi);
            abiStr.append(',');
        }
        return abiStr.toString();
    }

    private static String getCpuName2() {
        try {
            FileReader e = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(e);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            e.close();
            br.close();
            return array[1];
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }
}
