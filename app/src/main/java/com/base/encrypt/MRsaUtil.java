package com.base.encrypt;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * RSA签名
 * 注：原代码来源于http://www.oschina.net/code/snippet_1611_4789，自己稍做了一些修改
 * @author wangzp
 */
public class MRsaUtil {

    /** 算法名称 */
    private static final String ALGORITHOM = "RSA";

    /**保存生成的密钥对的文件名称。 */
    private static final String RSA_PAIR_FILENAME = "/__RSA_PAIR.txt";

    /** 密钥大小 */
    private static final int KEY_SIZE = 1024;

    /** 默认的安全服务提供者 */
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

    private static KeyPairGenerator keyPairGen = null;

    private static KeyFactory keyFactory = null;

    /** 缓存的密钥对。 */
    private static KeyPair oneKeyPair = null;

    private static File rsaPairFile = null;

    static {
        try {
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
            keyFactory = KeyFactory.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        } catch (NoSuchAlgorithmException ex) {
        }
        rsaPairFile = new File(getRSAPairFilePath());
    }

    private MRsaUtil() { }

    /**
     * 生成并返回RSA密钥对。
     */
    private static synchronized KeyPair generateKeyPair() {
        try {
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            oneKeyPair = keyPairGen.generateKeyPair();
            saveKeyPair(oneKeyPair);
            return oneKeyPair;
        } catch (InvalidParameterException ex) {
        } catch (NullPointerException ex) {
        }
        return null;
    }

    /**
     * 返回生成/读取的密钥对文件的路径。
     */
    private static String getRSAPairFilePath() {
        String urlPath = MRsaUtil.class.getResource("/").getPath();
        return (new File(urlPath).getParent() + RSA_PAIR_FILENAME);
    }

    /**
     * 若需要创建新的密钥对文件，则返回 {@code true}，否则 {@code false}。
     */
    private static boolean isCreateKeyPairFile() {
        // 是否创建新的密钥对文件
        boolean createNewKeyPair = false;
        if (!rsaPairFile.exists() || rsaPairFile.isDirectory()) {
            createNewKeyPair = true;
        }
        return createNewKeyPair;
    }

    /**
     * 将指定的RSA密钥对以文件形式保存。
     *
     * @param keyPair 要保存的密钥对。
     */
    private static void saveKeyPair(KeyPair keyPair) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = FileUtils.openOutputStream(rsaPairFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(keyPair);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 返回RSA密钥对。
     */
    public static KeyPair getKeyPair() {
        // 首先判断是否需要重新生成新的密钥对文件
        if (isCreateKeyPairFile()) {
            // 直接强制生成密钥对文件，并存入缓存。
            return generateKeyPair();
        }
        if (oneKeyPair != null) {
            return oneKeyPair;
        }
        return readKeyPair();
    }

    // 同步读出保存的密钥对
    private static KeyPair readKeyPair() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = FileUtils.openInputStream(rsaPairFile);
            ois = new ObjectInputStream(fis);
            oneKeyPair = (KeyPair) ois.readObject();
            return oneKeyPair;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * 根据给定的系数和专用指数构造一个RSA专用的公钥对象。
     *
     * @param modulus 系数。
     * @param publicExponent 专用指数。
     * @return RSA专用公钥对象。
     */
    public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) {
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus),
                new BigInteger(publicExponent));
        try {
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException ex) {
        } catch (NullPointerException ex) {
        }
        return null;
    }

    /**
     * 根据给定的系数和专用指数构造一个RSA专用的私钥对象。
     *
     * @param modulus 系数。
     * @param privateExponent 专用指数。
     * @return RSA专用私钥对象。
     */
    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) {
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus),
                new BigInteger(privateExponent));
        try {
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException ex) {
        } catch (NullPointerException ex) {
        }
        return null;
    }

    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的私钥对象。
     *
     * @param hexModulus 系数。
     * @param hexPrivateExponent 专用指数。
     * @return RSA专用私钥对象。
     */
    public static RSAPrivateKey getRSAPrivateKey(String hexModulus, String hexPrivateExponent) {
        if(StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPrivateExponent)) {
            return null;
        }
        byte[] modulus = null;
        byte[] privateExponent = null;
        try {
            modulus = Hex.decodeHex(hexModulus.toCharArray());
            privateExponent = Hex.decodeHex(hexPrivateExponent.toCharArray());
        } catch(DecoderException ex) {
        }
        if(modulus != null && privateExponent != null) {
            return generateRSAPrivateKey(modulus, privateExponent);
        }
        return null;
    }

    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象。
     *
     * @param hexModulus 系数。
     * @param hexPublicExponent 专用指数。
     * @return RSA专用公钥对象。
     */
    public static RSAPublicKey getRSAPublidKey(String hexModulus, String hexPublicExponent) {
        if(StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPublicExponent)) {
            return null;
        }
        byte[] modulus = null;
        byte[] publicExponent = null;
        try {
            modulus = Hex.decodeHex(hexModulus.toCharArray());
            publicExponent = Hex.decodeHex(hexPublicExponent.toCharArray());
        } catch(DecoderException ex) {
        }
        if(modulus != null && publicExponent != null) {
            return generateRSAPublicKey(modulus, publicExponent);
        }
        return null;
    }

    /**
     * 使用指定的公钥加密数据。
     *
     * @param publicKey 给定的公钥。
     * @param data 要加密的数据。
     * @return 加密后的数据。
     */
    public static String encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);

        // 模长
        int MaxBlockSize = KEY_SIZE / 8;
        // 加密数据长度 <= 模长-11
        int len = (MaxBlockSize - 11) / 8;
        String[] datas = splitString(new String(data), len);
        StringBuffer mi = new StringBuffer();
        for (String s : datas) {
            mi.append(bcd2Str(ci.doFinal(s.getBytes())));
        }
        return mi.toString();

        //return ci.doFinal(data);
    }

    /**
     * 使用指定的私钥解密数据。
     *
     * @param privateKey 给定的私钥。
     * @param data 要解密的数据。
     * @return 原数据。
     */
    public static String decrypt(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);

        // 模长
        int key_len = KEY_SIZE / 8;
        byte[] bcd = ASCII2BCD(data, data.length);
        StringBuffer sBuffer = new StringBuffer();
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            sBuffer.append(new String(ci.doFinal(arr)));
        }
        return sBuffer.toString();
        //return ci.doFinal(data);
    }

    /**
     * 使用给定的公钥加密给定的字符串。
     * <p />
     * 若 {@code publicKey} 为 {@code null}，或者 {@code plaintext} 为 {@code null} 则返回 {@code
     * null}。
     *
     * @param publicKey 给定的公钥。
     * @param plaintext 字符串。
     * @return 给定字符串的密文。
     */
    public static String encryptString(PublicKey publicKey, String plaintext) {
        if (publicKey == null || plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        try {
            String en_data = encrypt(publicKey, data);
            return new String(Hex.encodeHex(en_data.getBytes()));
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * 使用默认的公钥加密给定的字符串。
     * <p />
     * 若{@code plaintext} 为 {@code null} 则返回 {@code null}。
     *
     * @param plaintext 字符串。
     * @return 给定字符串的密文。
     */
    public static String encryptString(String plaintext) {
        if(plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        KeyPair keyPair = getKeyPair();
        try {
           String en_data = encrypt((RSAPublicKey)keyPair.getPublic(), data);
            return new String(Hex.encodeHex(en_data.getBytes()));
        } catch(NullPointerException ex) {
        } catch(Exception ex) {
        }
        return null;
    }

    /**
     * 使用给定的私钥解密给定的字符串。
     * <p />
     * 若私钥为 {@code null}，或者 {@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。
     * 私钥不匹配时，返回 {@code null}。
     *
     * @param privateKey 给定的私钥。
     * @param encrypttext 密文。
     * @return 原文字符串。
     */
    public static String decryptString(PrivateKey privateKey, String encrypttext) {
        if (privateKey == null || StringUtils.isBlank(encrypttext)) {
            return null;
        }
        try {
            byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
            String data = decrypt(privateKey, en_data);
            return data;
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * 使用默认的私钥解密给定的字符串。
     * <p />
     * 若{@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。
     * 私钥不匹配时，返回 {@code null}。
     *
     * @param encrypttext 密文。
     * @return 原文字符串。
     */
    public static String decryptString(String encrypttext) {
        if(StringUtils.isBlank(encrypttext)) {
            return null;
        }
        KeyPair keyPair = getKeyPair();
        try {
            byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
            String data = decrypt((RSAPrivateKey)keyPair.getPrivate(), en_data);
            return data;
        } catch(NullPointerException ex) {
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * 使用默认的私钥解密由JS加密（使用此类提供的公钥加密）的字符串。
     *
     * @param encrypttext 密文。
     * @return {@code encrypttext} 的原文字符串。
     */
    public static String decryptStringByJs(String encrypttext) {
        String text = decryptString(encrypttext);
        if(text == null) {
            return null;
        }
        return StringUtils.reverse(text);
    }
    /**
     * 与文中参考的博客不同，添加了该方法
     * @param privateKey
     * @param encrypttext
     * @return
     */
    public static String decryptStringByJs(RSAPrivateKey privateKey, String encrypttext) {
        String text = decryptString(privateKey, encrypttext);
        if(text == null) {
            return null;
        }
        return StringUtils.reverse(text);
    }


    /** 返回已初始化的默认的公钥。*/
    public static RSAPublicKey getDefaultPublicKey() {
        KeyPair keyPair = getKeyPair();
        if(keyPair != null) {
            return (RSAPublicKey)keyPair.getPublic();
        }
        return null;
    }

    /** 返回已初始化的默认的私钥。*/
    public static RSAPrivateKey getDefaultPrivateKey() {
        KeyPair keyPair = getKeyPair();
        if (keyPair != null) {
            return (RSAPrivateKey) keyPair.getPrivate();
        }
        return null;
    }

    /**
     * 字符串分片
     *
     * @param string
     *            源字符串
     * @param len
     *            单片的长度（keysize/8）
     * @return
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * bcd 转 Str
     *
     * @param bytes
     * @return
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;
        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * ASCII 转 BCD
     *
     * @param ascii
     * @param asc_len
     * @return
     */
    public static byte[] ASCII2BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc2bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc2bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    /**
     * asc转bcd
     *
     * @param asc
     * @return
     */
    public static byte asc2bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    /**
     * 字节数组分片
     *
     * @param data
     * @param len
     * @return
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    public static void main (String[]args) throws DecoderException {
        RSAPublicKey publicKey = getDefaultPublicKey();
        RSAPrivateKey privateKey = getDefaultPrivateKey();

        //明文
        String data = "{\"returnState\":true,\"data\":{\"message\":\"查询成功\",\"statue\":1,\"data\":[{\"id\":119,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013544_2NMT8YWAW0.jpg\"},{\"id\":120,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013552_43YDAAMOFU.jpg\"},{\"id\":121,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226015552_LNF8PQUNYN.jpg\"}]}}";
        data=data+data;
        String codeString = encryptString(publicKey, data);
        System.out.println("公钥加密："+codeString);
        System.out.println("私钥解密："+decryptString(privateKey, codeString));

    }
}