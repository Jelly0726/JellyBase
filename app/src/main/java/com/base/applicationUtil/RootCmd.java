package com.base.applicationUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * 执行linux命令
 *
 *
 String paramString= "adb push MySMS.apk /system/app" +"\n"+
                    "adb shell" +"\n"+
                    "su" +"\n"+
                    "mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system" +"\n"+
                    "cat /sdcard/MySMS.apk > /system/app/MySMS.apk" +"\n"+
                    "mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system" +"\n"+
                    "exit" +"\n"+
                    "exit";
 if(RootCmd.haveRoot()){
    if(RootCmd.execRootCmdSilent(paramString)==-1){
        Toast.makeText(this, "安装不成功", Toast.LENGTH_LONG).show();
    }else{
        Toast.makeText(this, "安装成功", Toast.LENGTH_LONG).show();
    }
 }else{
    Toast.makeText(this, "没有root权限", Toast.LENGTH_LONG).show();
 }
 *
 *
 *
 */
public final class RootCmd {
    // 执行linux命令并且输出结果
    protected static String execRootCmd(String paramString) {
        String result = "result : ";
        try {
            Process localProcess = Runtime.getRuntime().exec("su ");// 经过Root处理的android系统即有su命令
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    localOutputStream);
            InputStream localInputStream = localProcess.getInputStream();
            DataInputStream localDataInputStream = new DataInputStream(
                    localInputStream);
            String str1 = String.valueOf(paramString);
            String str2 = str1 + "\n";
            localDataOutputStream.writeBytes(str2);
            localDataOutputStream.flush();
            String str3 = null;
//            while ((str3 = localDataInputStream.readLine()) != null) {
//                Log.d("result", str3);
//            }
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            return result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return result;
        }
    }

    // 执行linux命令但不关注结果输出
    protected static int execRootCmdSilent(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            int result = localProcess.exitValue();
            return (Integer) result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }

    // 判断机器Android是否已经root，即是否获取root权限
    protected static boolean haveRoot() {

        int i = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
        if (i != -1) {
            return true;
        }
        return false;
    }

}