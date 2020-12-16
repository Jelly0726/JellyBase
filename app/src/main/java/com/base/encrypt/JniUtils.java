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
     * AES的Encrypt加密
     *
     * @param src
     * @return
     */
    public native byte[] encodeByAESEncrypt(Context context, byte[] src);

    /**
     * AES的Encrypt解密
     *
     * @param src
     * @return
     */
    public native byte[] decodeByAESEncrypt(Context context, byte[] src);
    /**
     * AES的Encrypt加密
     *
     * @param src
     * @return
     */
    public native byte[] encryptByAESEncrypt(Context context, byte[] src, byte[] key);

    /**
     * AES的Encrypt解密
     *
     * @param src
     * @return
     */
    public native byte[] decryptByAESEncrypt(Context context, byte[] src, byte[] key);
    /**
     * AES的Cipher加密
     *
     * @param src
     * @return
     */
    public native byte[] encodeByAESCipher(Context context, byte[] src);

    /**
     * AES的Cipher解密
     *
     * @param src
     * @return
     */
    public native byte[] decodeByAESCipher(Context context, byte[] src);
    /**
     * AES的Cipher加密
     *
     * @param src
     * @return
     */
    public native byte[] encryptAESCipher(Context context, byte[] src, byte[] key);

    /**
     * AES的Cipher解密
     *
     * @param src
     * @return
     */
    public native byte[] decryptAESCipher(Context context, byte[] src, byte[] key);

    /**
     * RSA公钥加密
     *
     * @param src
     * @return
     */
    public native byte[] encodeByRSAPubKey(Context context, byte[] src);

    /**
     * RSA私钥解密
     *
     * @param src
     * @return
     */
    public native byte[] decodeByRSAPrivateKey(Context context, byte[] src);

    /**
     * RSA公钥加密
     *
     * @param publicKey  公钥base64字符串（没换行）
     * @param src        待加密的明文
     * @return
     */
    public native String encryptRSA(Context context,String publicKey,String src);

    /**
     * RSA私钥解密
     *
     * @param privateKey  私钥base64字符串（没换行）
     * @param src         待解密的base64密文
     * @return
     */
    public native String decryptRSA(Context context, String privateKey,String src);

    /**
     * RSA私钥签名
     *
     * @param src
     * @return
     */
    public native byte[] signByRSAPrivateKey(Context context, byte[] src);

    /**
     * RSA公钥验证签名
     *
     * @param src
     * @param sign
     * @return 1:验证成功
     */
    public native int verifyByRSAPubKey(Context context, byte[] src, byte[] sign);

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
