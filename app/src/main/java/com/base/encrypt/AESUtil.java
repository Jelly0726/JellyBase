package com.base.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具  加密结果为base64 、16进制编码格式
 */
public class AESUtil  extends Base64 {
    public static final String KEY_ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES = "AES/ECB/PKCS5Padding";////"算法/模式/补码方式"
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
            cipher = Cipher.getInstance(ALGORITHM_AES);
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
     * @param base64EncodedAESKey 经过BASE64加密后的AES秘钥
     * @param encryptedDataStr
     * @return
     */
    public static String decryptByAESAndBase64(String base64EncodedAESKey, String encryptedDataStr) {
        //如果的到的是16进制密文，别忘了先转为2进制再解密
        SecretKey secretKey = restoreAESKey(base64EncodedAESKey);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
            // 将加密组件的模式改为解密
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 和上面的加密相反，先解base64，再解密，最后将byte数组转为字符串
            String decryptedDataStr = new String(cipher.doFinal(decodeBase64(encryptedDataStr)));
            return decryptedDataStr;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("decryptByAESAndBase64="+e);
            return null;
        }

    }

    /**
     *
     * @Title: encryptByAESAndHexStr
     * @author：liuyx
     * @date：2016年5月10日上午9:40:37
     * @Description: 使用AES加密，并返回16进制的密文
     * @param base64EncodedAESKey  经过BASE64加密后的AES秘钥
     * @param dataStr
     * @return  返回16进制的AES秘钥
     */
    public static String encryptByAESAndHexStr(String base64EncodedAESKey, String dataStr) {
        SecretKey secretKey = restoreAESKey(base64EncodedAESKey);

        // 初始化加密组件
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 加密后的数据，首先将字符串转为byte数组，然后加密，为便于保存先转为base64

            String encryptedDataStr = Hex.encodeHexString(cipher.doFinal(dataStr.getBytes()));
            return encryptedDataStr;
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("encryptByAESAndHexStr="+e);
            return null;
        }
    }

    /**
     *
     * @Title: decryptByAESAndHexStr
     * @author：liuyx
     * @date：2016年5月10日上午11:24:47
     * @Description: 使用AES解密，并返回16进制的密文
     * @param base64EncodedAESKey
     * @param encryptedDataStr
     * @return
     */
    public static String decryptByAESAndHexStr(String base64EncodedAESKey, String encryptedDataStr) {
        //如果的到的是16进制密文，别忘了先转为2进制再解密
        try {
            byte[] twoStrResult = Hex.decodeHex(encryptedDataStr);
            SecretKey secretKey = restoreAESKey(base64EncodedAESKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
            // 将加密组件的模式改为解密
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 和上面的加密相反，先解base64，再解密，最后将byte数组转为字符串
            String decryptedDataStr = new String(cipher.doFinal(twoStrResult));
            return decryptedDataStr;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("decryptByAESAndHexStr="+e);
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
    public static void main(String[] args) {
        String str = "{\"returnState\":true,\"data\":{\"message\":\"查询成功\",\"statue\":1,\"data\":[{\"id\":119,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013544_2NMT8YWAW0.jpg\"},{\"id\":120,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013552_43YDAAMOFU.jpg\"},{\"id\":121,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226015552_LNF8PQUNYN.jpg\"}]}}";
        str=str+str;
        System.out.println("原字符串："+str);
        long star=System.currentTimeMillis();
        /*AES相关------start*/
        String aesKey = getAESRandomKeyString();
        System.out.println("生成AES秘钥时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String aesEncryptedStr = encryptByAESAndBase64(aesKey, str);
        System.out.println("AES加密后："+aesEncryptedStr);
        System.out.println("AES加密时间："+(System.currentTimeMillis()-star)+"ms");
        star=System.currentTimeMillis();
        String aesEncryp = encryptByAESAndHexStr(aesKey, str);
        System.out.println("AES加密后16进制："+aesEncryp);
        System.out.println("16进制AES加密时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String aesDecryptedStr = decryptByAESAndBase64(aesKey, aesEncryptedStr);
        System.out.println("AES解密后："+aesDecryptedStr);
        System.out.println("AES解密时间："+(System.currentTimeMillis()-star)+"ms");

        star=System.currentTimeMillis();
        String aesDecrypted = decryptByAESAndHexStr(aesKey, aesEncryp);
        System.out.println("AES解密后16进制："+aesDecrypted);
        System.out.println("16进制AES解密时间："+(System.currentTimeMillis()-star)+"ms");
        /*AES相关------end*/
    }
}
