package com.jelly.encrypt;


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
    public native String encodeByHmacSHA1(Context context, byte[] src);

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

//    /**
//     * AES的Encrypt加密
//     *
//     * @param src
//     * @return
//     */
//    public native String encodeByAESEncrypt(Context context, byte[] src);
//
//    /**
//     * AES的Encrypt解密
//     *
//     * @param src
//     * @return
//     */
//    public native String decodeByAESEncrypt(Context context, byte[] src);
//    /**
//     * AES的Encrypt加密
//     *
//     * @param src
//     * @return
//     */
//    public native String encryptByAESEncrypt(Context context, byte[] src, byte[] key);
//
//    /**
//     * AES的Encrypt解密
//     *
//     * @param src
//     * @return
//     */
//    public native String decryptByAESEncrypt(Context context, byte[] src, byte[] key);
//    /**
//     * AES的Cipher加密
//     *
//     * @param src
//     * @return
//     */
//    public native String encodeByAESCipher(Context context, byte[] src);
//
//    /**
//     * AES的Cipher解密
//     *
//     * @param src
//     * @return
//     */
//    public native String decodeByAESCipher(Context context, byte[] src);
//    /**
//     * AES的Cipher加密
//     *
//     * @param src
//     * @return
//     */
//    public native String encryptAESCipher(Context context, byte[] src, byte[] key);
//
//    /**
//     * AES的Cipher解密
//     *
//     * @param src
//     * @return
//     */
//    public native String decryptAESCipher(Context context, byte[] src, byte[] key);
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
    public native byte[] encodeByRSAPubKey(Context context,byte[] src);

    /**
     * RSA私钥解密
     *
     * @param src
     * @return
     */
    public native byte[] decodeByRSAPriKey(Context context, byte[] src);
    /**
     * RSA私钥加密
     *
     * @param src  待加密的明文（RSA私钥加密中文时明文长度小于5个字，解密末尾乱码
     *             ，加密非中文字符长度大于1，否则加密失败）
     * @return
     */
    public native byte[] encodeByRSAPriKey(Context context,byte[] src);

    /**
     * RSA公钥解密
     *
     * @param src  待解密的base64密文（RSA私钥加密中文时明文长度小于5个字，解密末尾乱码
     *             ，加密非中文字符长度大于1，否则加密失败）
     * @return
     */
    public native byte[] decodeByRSAPubKey(Context context, byte[] src);

    /**
     * RSA公钥加密
     *
     * @param src        待加密的明文
     * @param publicKey  公钥base64字符串（没换行）
     * @return
     */
    public native byte[] encryptRSA(Context context,byte[] src,byte[] publicKey);

    /**
     * RSA私钥解密
     *
     * @param src         待解密的base64密文
     * @param privateKey  私钥base64字符串（没换行）
     * @return
     */
    public native byte[] decryptRSA(Context context, byte[] src,byte[] privateKey);
    /**
     * RSA私钥加密
     *
     * @param src        待加密的明文 （RSA私钥加密中文时明文长度小于5个字，解密末尾乱码
     *                   ，加密非中文字符长度大于1，否则加密失败）
     * @param privateKey  私钥base64字符串（没换行）
     * @return
     */
    public native byte[] encodeByRSAPriKey(Context context,byte[] src,byte[] privateKey);

    /**
     * RSA公钥解密
     *
     * @param src         待解密的base64密文 （RSA私钥加密中文时明文长度小于5个字，解密末尾乱码
     *                    ，加密非中文字符长度大于1，否则加密失败）
     * @param publicKey  公钥base64字符串（没换行）
     * @return
     */
    public native byte[] decodeByRSAPubKey(Context context, byte[] src,byte[] publicKey);

    /**
     * MD5编码
     *
     * @param src
     * @return 默认小写
     */
    public native String md5(Context context,byte[] src);
    /**
     * 根据文件名读取Assets目录下的文件
     * 注：耗时io操作
     * @param fileName 文件名（含后缀）
     * @return
     */
    public native byte[] readAssets(Context context,String fileName);
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

    /**
     * 获取RSA秘钥对
     * @param context
     * @return
     */
    public native String[] generateRSAKey(Context context);
}
