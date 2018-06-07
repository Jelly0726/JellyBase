package com.base.encrypt;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @ClassName: EncryptUtils
 * @Description: AES、RSA加密、解密
 * @author: liuyx
 * @date: 2016年5月10日上午9:07:42
 */
public class EncryptUtils extends Base64 {

    public static void main(String[] args) {
        String str = "{\"returnState\":true,\"data\":{\"message\":\"查询成功\",\"statue\":1,\"data\":[{\"id\":119,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013544_2NMT8YWAW0.jpg\"},{\"id\":120,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013552_43YDAAMOFU.jpg\"},{\"id\":121,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226015552_LNF8PQUNYN.jpg\"}]}}";
        System.out.println("原字符串："+str);
        long star=System.currentTimeMillis();
        /*AES相关------start*/
        String aesKey = EncryptUtils.getAESRandomKeyString();
        System.out.println("生成AES秘钥时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String aesEncryptedStr = EncryptUtils.encryptByAESAndBase64(aesKey, str);
        System.out.println("AES加密后："+aesEncryptedStr);
        System.out.println("AES加密时间："+(System.currentTimeMillis()-star)+"ms");
        star=System.currentTimeMillis();
        String aesDecryptedStr = EncryptUtils.decryptByAESAndBase64(aesKey, aesEncryptedStr);
        System.out.println("AES解密后："+aesDecryptedStr);
        System.out.println("AES解密时间："+(System.currentTimeMillis()-star)+"ms");
        /*AES相关------end*/
        /*RSA相关------start*/
        star=System.currentTimeMillis();
        Map<String,String> keyPair = EncryptUtils.getRSARandomKeyPair();
        String publicRSAKey = keyPair.get(EncryptUtils.PUBLIC_KEY);
        String privateRSAKey = keyPair.get(EncryptUtils.PRIVATE_KEY);
        System.out.println("生成秘钥时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String encryptedStr = EncryptUtils.encryptByRSAPublicKeyAndBase64(publicRSAKey, str);
        System.out.println("公钥加密后:"+encryptedStr);
        System.out.println("公钥加密时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String decryptedStr = EncryptUtils.decryptByRSAPrivateKeyAndBase64(privateRSAKey, encryptedStr);
        System.out.println("私钥解密后:"+decryptedStr);
        System.out.println("私钥解密时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        encryptedStr = EncryptUtils.encryptByRSAPrivateKeyAndBase64(privateRSAKey, str);
        System.out.println("私钥加密后:"+encryptedStr);
        System.out.println("私钥加密时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String sign = EncryptUtils.getSignFromEncryptedDataWithPrivateKey(privateRSAKey, encryptedStr);
        System.out.println("签名:"+sign);
        System.out.println("签名时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        boolean verifyResult = EncryptUtils.verifySign(publicRSAKey, sign, encryptedStr);
        System.out.println("签名验证结果："+verifyResult);
        System.out.println("签名验证时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        decryptedStr = EncryptUtils.decryptByRSAPublicKeyAndBase64(publicRSAKey, encryptedStr);
        System.out.println("公钥解密后:"+decryptedStr);
        System.out.println("公钥解密时间："+(System.currentTimeMillis()-star)+"ms");
        /*RSA相关------end*/
    }

    public static final String KEY_ALGORITHM_RSA = "RSA";
    public static final String KEY_ALGORITHM_AES = "AES";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    public static final String SIGNATURE_ALGORITHM_MD5_RSA = "MD5withRSA";
    /**
     *
     * @Title: getCRC32Value
     * @author：liuyx @date：2016年5月10日09:19:29
     * @Description: 获取字符串对应的重复概率较小的整形
     * @param str
     *            传入32位字符串
     * @return
     */
    public static String getCRC32Value(String str) {
        CRC32 crc32 = new CRC32();
        crc32.update(str.getBytes());
        return Long.toString(crc32.getValue());
    }

    /*AES相关------start*/
    /**
     *
     * @Title: getAesRandomKeyString
     * @author：liuyx
     * @date：2016年5月10日上午9:30:15
     * @Description: 获取AES随机密钥字符串
     * @return
     */
    public static String getAESRandomKeyString() {
        // 随机生成密钥
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance(KEY_ALGORITHM_AES);
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            Key key = keygen.generateKey();
            // 获取秘钥字符串
            String key64Str = encodeBase64String(key.getEncoded());
            return key64Str;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("getAESRandomKeyString="+e);
            return null;
        }

    }

    /**
     *
     * @Title: encryptByAESAndBase64
     * @author：liuyx
     * @date：2016年5月10日上午9:40:37
     * @Description: 使用AES加密，并返回经过BASE64处理后的密文
     * @param base64EncodedAESKey
     *            经过BASE64加密后的AES秘钥
     * @param dataStr
     * @return
     */
    public static String encryptByAESAndBase64(String base64EncodedAESKey, String dataStr) {
        SecretKey secretKey = restoreAESKey(base64EncodedAESKey);

        // 初始化加密组件
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(KEY_ALGORITHM_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 加密后的数据，首先将字符串转为byte数组，然后加密，为便于保存先转为base64
            String encryptedDataStr = encodeBase64String(cipher.doFinal(dataStr.getBytes()));
            return encryptedDataStr;
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("encryptByAESAndBase64="+e);
            return null;
        }
    }

    /**
     *
     * @Title: decryptByAESAndBase64
     * @author：liuyx
     * @date：2016年5月10日上午11:24:47
     * @Description: 使用AES解密，并返回经过BASE64处理后的密文
     * @param base64EncodedAESKey
     * @param encryptedDataStr
     * @return
     */
    public static String decryptByAESAndBase64(String base64EncodedAESKey, String encryptedDataStr) {
        SecretKey secretKey = restoreAESKey(base64EncodedAESKey);
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_AES);
            // 将加密组件的模式改为解密
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 和上面的加密相反，先解base64，再解密，最后将byte数组转为字符串
            String decryptedDataStr = new String(cipher.doFinal(Base64.decodeBase64(encryptedDataStr)));
            return decryptedDataStr;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("decryptByAESAndBase64="+e);
            return null;
        }

    }

    /**
     *
     * @Title: restoreAESKey
     * @author：liuyx
     * @date：2016年5月10日上午9:53:01
     * @Description: 还原BASE64加密后的AES密钥
     * @param base64EncodedAESKey
     * @return
     */
    public static SecretKey restoreAESKey(String base64EncodedAESKey) {
        // 还原秘钥字符串到秘钥byte数组
        byte[] keyByteArray = decodeBase64(base64EncodedAESKey);
        // 重新形成秘钥，SecretKey是Key的子类
        SecretKey secretKey = new SecretKeySpec(keyByteArray, KEY_ALGORITHM_AES);
        return secretKey;
    }

    /*AES相关------end*/


    /*RSA相关------start*/
    /**
     *
     * @Title: getRSARandomKeyPair
     * @author：liuyx
     * @date：2016年5月10日上午10:00:04
     * @Description: 生成RSA密钥对
     * @return
     */
    public static Map getRSARandomKeyPair() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            String publicKeyStr = encodeBase64String(publicKey.getEncoded());
            String privateKeyStr = encodeBase64String(privateKey.getEncoded());

            Map<String, String> keyMap = new HashMap<String, String>(2);
            keyMap.put(PUBLIC_KEY, publicKeyStr);
            keyMap.put(PRIVATE_KEY, privateKeyStr);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            System.out.println("getRSARandomKeyPair="+e);
            return null;
        }

    }

    /**
     *
     * @Title: encryptByRSAPublicKeyAndBase64
     * @author：liuyx
     * @date：2016年5月10日上午10:50:00
     * @Description: RSA公钥加密：使用RSA公钥（BASE64加密后的字符串）对数据进行加密
     * @param publicRSAKey
     * @param dataStr
     * @return
     */
    public static String encryptByRSAPublicKeyAndBase64(String publicRSAKey,String dataStr) {
        byte[] data = dataStr.getBytes();
        // 对公钥解密
        Key decodePublicKey = restoreRSAPublicKeyFromBase64KeyEncodeStr(publicRSAKey);

        // 对数据加密
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey);
            //byte[] encodedData = cipher.doFinal(data);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            String encodedDataStr = encodeBase64String(encryptedData);
            return encodedDataStr;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("encryptByRSAPublicKeyAndBase64="+e);
            return null;
        }

    }
    /**
     *
     * @Title: decryptByRSAPublicKeyAndBase64
     * @author：liuyx
     * @date：2016年5月10日上午10:56:03
     * @Description: RSA公钥解密：使用RSA公钥（BASE64加密后的字符串）对数据进行解密
     * @param publicRSAKey
     * @param encryptedDataStr
     * @return
     */
    public static String decryptByRSAPublicKeyAndBase64(String publicRSAKey, String encryptedDataStr) {
        //还原公钥
        Key decodePublicKey = restoreRSAPublicKeyFromBase64KeyEncodeStr(publicRSAKey);
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, decodePublicKey);
            //byte[] decodedData = cipher.doFinal();
            byte[] decodedData = Base64.decodeBase64(encryptedDataStr);
            int inputLen = decodedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(decodedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(decodedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();


            String decodedDataStr = new String(decryptedData);
            return decodedDataStr;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("decryptByRSAPublicKeyAndBase64="+e);
            return null;
        }

    }

    /**
     *
     * @Title: getSignFromEncryptedDataWithPrivateKey
     * @author：liuyx
     * @date：2016年5月10日上午11:08:13
     * @Description: 生成RSA签名：使用base64加密后的私钥和加密后的数据（byte数组形式），获取加密数据签名
     * @param privateRSAKey
     * @param encryotedDataStr
     * @return
     */
    public static String getSignFromEncryptedDataWithPrivateKey(String privateRSAKey,String encryotedDataStr) {
        byte[] data = decodeBase64(encryotedDataStr);
        //加密后的数据+私钥，生成签名
        Key decodePrivateKey = restoreRSAPrivateKeyFromBase64KeyEncodeStr(privateRSAKey);
        Signature signature;
        try {
            signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5_RSA);
            signature.initSign((PrivateKey)decodePrivateKey);  //用的是私钥
            signature.update(data);  //用的是加密后的数据字节数组
            //取得签名
            String sign = Base64.encodeBase64String((signature.sign()));
            return sign;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("getSignFromEncryptedDataWithPrivateKey="+e);
            return null;
        }

    }

    /**
     *
     * @Title: verifySign
     * @author：liuyx
     * @date：2016年5月10日上午11:18:06
     * @Description: 验证RSA签名
     * @param publicRSAKey
     * @param sign
     * @param encryotedDataStr
     * @return
     */
    public static boolean verifySign(String publicRSAKey,String sign,String encryotedDataStr) {
        byte[] data = decodeBase64(encryotedDataStr);
        Key decodePublicKey = restoreRSAPublicKeyFromBase64KeyEncodeStr(publicRSAKey);
        //初始化验证签名
        Signature signature;
        try {
            signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5_RSA);
            signature.initVerify((PublicKey )decodePublicKey);  //用的是公钥
            signature.update(data);  //用的是加密后的数据字节数组
            boolean ret = signature.verify(decodeBase64(sign));
            return ret;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("verifySign="+e);
        }

        return false;
    }

    /**
     *
     * @Title: encryptByRSAPrivateKeyAndBase64
     * @author：liuyx
     * @date：2016年5月10日上午11:00:03
     * @Description: RSA私钥加密：使用RSA私钥（BASE64加密后的字符串）对数据进行加密
     * @param privateRSAKey
     * @param dataStr
     * @return
     */
    public static String encryptByRSAPrivateKeyAndBase64(String privateRSAKey,String dataStr) {
        byte[] data = dataStr.getBytes();
        // 对私钥解密
        Key decodePrivateKey = restoreRSAPrivateKeyFromBase64KeyEncodeStr(privateRSAKey);

        // 对数据加密
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, decodePrivateKey);
            //byte[] encodedData = cipher.doFinal(data);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            String encodedDataStr = encodeBase64String(encryptedData);
            return encodedDataStr;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("encryptByRSAPrivateKeyAndBase64="+e);
            return null;
        }

    }

    /**
     *
     * @Title: decryptByRSAPrivateKeyAndBase64
     * @author：liuyx
     * @date：2016年5月10日上午11:26:44
     * @Description: RSA私钥解密:使用RSA私钥（BASE64加密后的字符串）对数据进行解密
     * @param privateRSAKey
     * @param encryptedDataStr
     * @return
     */
    public static String decryptByRSAPrivateKeyAndBase64(String privateRSAKey, String encryptedDataStr) {
        //还原私钥
        Key decodePrivateKey = restoreRSAPrivateKeyFromBase64KeyEncodeStr(privateRSAKey);
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey);
           // byte[] decodedData = cipher.doFinal(Base64.decodeBase64(encryptedDataStr));
            byte[] encryptedData = Base64.decodeBase64(encryptedDataStr);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            String decodedDataStr = new String(decryptedData);
            return decodedDataStr;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("decryptByRSAPrivateKeyAndBase64="+e);
            return null;
        }

    }

    /*
     * 获取base64加密后的字符串的原始公钥
     */
    private static Key restoreRSAPublicKeyFromBase64KeyEncodeStr(String keyStr) {
        byte[] keyBytes = Base64.decodeBase64(keyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        Key publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("获取base64加密后的字符串的原始公钥="+e);
        }
        return publicKey;
    }

    /*
     * 获取base64加密后的字符串的原始私钥
     */
    private static Key restoreRSAPrivateKeyFromBase64KeyEncodeStr(String keyStr) {
        byte[] keyBytes = Base64.decodeBase64(keyStr);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        Key privateKey=null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
            privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("获取base64加密后的字符串的原始私钥="+e);
        }
        return privateKey;
    }
    /*RSA相关------end*/
}