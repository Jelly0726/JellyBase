package com.base.encrypt;


import android.content.Context;

public class JniUtils {

    static {
        System.loadLibrary("crypto");
        System.loadLibrary("cipher");
    }

    /**
     * HmacSHA1签名
     *
     * @param context
     * @param src
     * @return
     */
    public native byte[] encodeByHmacSHA1(Context context, byte[] src);

    /**
     * SHA1签名
     *
     * @param src
     * @return
     */
    public native String encodeBySHA1(Context context,byte[] src);

    /**
     * SHA224签名
     *
     * @param src
     * @return
     */
    public native String encodeBySHA224(Context context,byte[] src);

    /**
     * SHA384签名
     *
     * @param src
     * @return
     */
    public native String encodeBySHA256(Context context,byte[] src);

    /**
     * SHA256签名
     *
     * @param src
     * @return
     */
    public native String encodeBySHA384(Context context,byte[] src);

    /**
     * SHA512签名
     *
     * @param src
     * @return
     */
    public native String encodeBySHA512(Context context,byte[] src);

    /**
     * AES加密
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] encodeByAES(Context context,byte[] keys, byte[] src);

    /**
     * AES解密
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] decodeByAES(Context context,byte[] keys, byte[] src);

    /**
     * RSA公钥加密
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] encodeByRSAPubKey(Context context,byte[] keys, byte[] src);

    /**
     * RSA私钥解密
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] decodeByRSAPrivateKey(Context context,byte[] keys, byte[] src);

    /**
     * RSA私钥加密
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] encodeByRSAPrivateKey(Context context,byte[] keys, byte[] src);

    /**
     * RSA公钥解密
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] decodeByRSAPubKey(Context context,byte[] keys, byte[] src);

    /**
     * RSA私钥签名
     *
     * @param keys
     * @param src
     * @return
     */
    public native byte[] signByRSAPrivateKey(Context context,byte[] keys, byte[] src);

    /**
     * RSA公钥验证签名
     *
     * @param keys
     * @param src
     * @param sign
     * @return 1:验证成功
     */
    public native int verifyByRSAPubKey(Context context,byte[] keys, byte[] src, byte[] sign);

    /**
     * 异或加解密
     *
     * @param src
     * @return
     */
    public native byte[] xOr(Context context,byte[] src);

    /**
     * MD5编码
     *
     * @param src
     * @return 默认小写
     */
    public native String md5(Context context,byte[] src);

    /**
     * 获取apk-sha1
     *
     * @param context
     * @return
     */
    public native String sha1OfApk(Context context);

    /**
     * 校验apk签名是否有效
     *
     * @param context
     * @return
     */
    public native boolean verifySha1OfApk(Context context);
}
