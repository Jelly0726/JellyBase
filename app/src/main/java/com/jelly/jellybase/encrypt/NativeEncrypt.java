package com.jelly.jellybase.encrypt;

import android.util.Base64;

public class NativeEncrypt {
    static {
        System.loadLibrary("native-lib");
    }
    public static void main(String[] args){
        String olde="和第四回佛山的化工i";
        System.out.println("原文:"+olde);
        System.out.println("加密后:"+nativeEncode(olde));
        System.out.println("解密后:"+nativeDecode(olde));
        System.out.println("签名后:"+nativeSign(olde));
        System.out.println("getKey:"+getKey());
        System.out.println("getSalt:"+getSalt());

    }
    private static String nativeEncode(String str) {
        byte[] enData = SecureUtil.encryptData(str.getBytes());
        String result = new String(Base64.encode(enData, Base64.DEFAULT));
        return result;
    }

    private static String nativeDecode(String str) {
        byte[] deData = Base64.decode(str, Base64.DEFAULT);
        byte[] oriData = SecureUtil.decryptData(deData);
        String oriStr = new String(oriData);
        return oriStr;
    }
    private static String nativeSign(String param) {
        return SecureUtil.getSign(param);
    }
    private static String getKey() {
        return "appKey" + SecureUtil.getDeviceId() + "appKey";
    }

    private static String getSalt() {
        return SecureUtil.getDeviceId() + "appKey" + SecureUtil.getDeviceId();
    }
}
