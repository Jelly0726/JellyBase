package com.base.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA加密，支持分片加密  加密结果为 BCD码
 * <pre>
 * BCD码（Binary-Coded Decimal‎）亦称二进码十进数或二-十进制代码。
 * 用4位二进制数来表示1位十进制数中的0~9这10个数码。
 * 是一种二进制的数字编码形式，用二进制编码的十进制代码。
 * 注：日常所说的BCD码大都是指8421BCD码形式
 * 注意：【此代码用了RSA/ECB/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
 * @author Ming
 *
 */
public class RSAUtil {
    /** 指定加密算法为RSA */
    private static String ALGORITHM = "RSA/ECB/PKCS1Padding";
    /** 指定key的大小 */
    private static int KEYSIZE = 1024;
    /** 指定公钥存放文件 */
    private static String PUBLIC_KEY_FILE = "PublicKey";
    /** 指定私钥存放文件 */
    private static String PRIVATE_KEY_FILE = "PrivateKey";
    /**密钥算法*/
    private static final String KEY_ALGORITHM = "RSA";
    /**签名加密算法*/
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    /** 默认的安全服务提供者 */
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();
    /** 公钥 */
    private static RSAPublicKey rsaPublicKey = null;
    /** 私钥 */
    private static RSAPrivateKey rsaPrivateKey = null;

    /**
     * 生成密钥对
     */
    public static void generateKeyPair()  {
        try {
            if (!fileIsExists(PUBLIC_KEY_FILE)
                    || !fileIsExists(PRIVATE_KEY_FILE)) {
                /** RSA算法要求有一个可信任的随机数源 */
                SecureRandom sr = new SecureRandom();
                /** 为RSA算法创建一个KeyPairGenerator对象 */
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM, DEFAULT_PROVIDER);
                /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
                kpg.initialize(KEYSIZE, sr);
                /** 生成密匙对 */
                KeyPair kp = kpg.generateKeyPair();
                /** 得到公钥 */
                rsaPublicKey= (RSAPublicKey) kp.getPublic();
                /** 得到私钥 */
                rsaPrivateKey= (RSAPrivateKey) kp.getPrivate();
                /** 用对象流将生成的密钥写入文件 */
                ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
                ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
                oos1.writeObject(rsaPublicKey);
                oos2.writeObject(rsaPrivateKey);
                /** 清空缓存，关闭文件输出流 */
                oos1.close();
                oos2.close();
            }
        }catch (Exception e){
            System.err.println("生成密钥对:"+e);
        }
    }
    /**
     * 判断文件是否存在
     * @param strFile
     * @return
     */
    private static boolean fileIsExists(String strFile){
        try{
            File f=new File(strFile);
            if(!f.exists()){
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
    /** 将文件中的公钥对象读出 */
    public static String getpublickey() {
        if (rsaPublicKey!=null){
            String publickey = encryptBASE64(rsaPublicKey.getEncoded());
            return publickey;
        }
        generateKeyPair();
        try {
            @SuppressWarnings("resource")
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            rsaPublicKey = (RSAPublicKey) ois.readObject();
            String publickey = encryptBASE64(rsaPublicKey.getEncoded());
            return publickey;
        } catch (Exception e) {
            System.out.println("将文件中的公钥对象读出:"+e);
        }
        return null;
    }

    /** 将文件中的私钥对象读出 */
    public static String getprivatekey(){
        if (rsaPrivateKey!=null){
            String privatekey = encryptBASE64(rsaPrivateKey.getEncoded());
            return privatekey;
        }
        generateKeyPair();
        try {
            @SuppressWarnings("resource")
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            rsaPrivateKey = (RSAPrivateKey) ois.readObject();
            String privatekey = encryptBASE64(rsaPrivateKey.getEncoded());
            return privatekey;
        } catch (Exception e) {
            System.out.println("将文件中的私钥对象读出:"+e);
        }
        return null;
    }
    /** 将文件中的公钥对象读出 */
    public static RSAPublicKey getRSAPublickey() {
        if (rsaPublicKey!=null){
            return rsaPublicKey;
        }
        generateKeyPair();
        try {
            @SuppressWarnings("resource")
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            rsaPublicKey = (RSAPublicKey) ois.readObject();
            return rsaPublicKey;
        } catch (Exception e) {
            System.out.println("将文件中的公钥对象读出:"+e);
        }
        return null;
    }

    /** 将文件中的私钥对象读出 */
    public static RSAPrivateKey getRSAPrivatekey(){
        if (rsaPrivateKey!=null){
            return rsaPrivateKey;
        }
        generateKeyPair();
        try {
            @SuppressWarnings("resource")
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            rsaPrivateKey = (RSAPrivateKey) ois.readObject();
            return rsaPrivateKey;
        } catch (Exception e) {
            System.out.println("将文件中的私钥对象读出:"+e);
        }
        return null;
    }
    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     * @param modulus 模
     * @param exponent 指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, DEFAULT_PROVIDER);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            System.out.println("使用模和指数生成RSA公钥:"+e);
        }
        return null;
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     * @param modulus    模
     * @param exponent   指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, DEFAULT_PROVIDER);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            System.out.println("使用模和指数生成RSA私钥:"+e);
        }
        return null;
    }
    /**
     * 由base64编码的私钥产生签名
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) {
        try {
            // 解密由base64编码的私钥
            byte[] keyBytes = decryptBASE64(privateKey);

            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

            // KEY_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, DEFAULT_PROVIDER);

            // 取私钥对象
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(data);

            return encryptBASE64(signature.sign());
        }catch (Exception e){
            System.err.println("由base64编码的私钥产生签名:"+e);
        }
        return null;
    }
    /**
     * 由base64编码的公钥验证签名
     *
     * @param data
     * @param publicKey
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign){
        try {
            // 解密由base64编码的公钥
            byte[] keyBytes = decryptBASE64(publicKey);

            // 构造X509EncodedKeySpec对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

            // KEY_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, DEFAULT_PROVIDER);

            // 取公钥对象
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(data);

            // 验证签名是否有效
            return signature.verify(decryptBASE64(sign));
        }catch (Exception e){
            System.err.println("由base64编码的公钥验证签名:"+e);
        }
        return false;
    }
    /**
     * 由私钥产生签名
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, RSAPrivateKey privateKey) {
        try {
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);

            return encryptBASE64(signature.sign());
        }catch (Exception e){
            System.err.println("由私钥产生签名:"+e);
        }
        return null;
    }
    /**
     * 由公钥验证签名
     *
     * @param data
     * @param publicKey
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, RSAPublicKey publicKey, String sign){
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            // 验证签名是否有效
            return signature.verify(decryptBASE64(sign));
        }catch (Exception e){
            System.err.println("由公钥验证签名:"+e);
        }
        return false;
    }
    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key)  {
        return new Base64().decode(key);
    }

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key)  {
        return new String(new Base64().encode(key));
    }

    /**
     * 公钥加密方法
     * @param source 数据源
     * @return       加密后的密文
     */
    public static String encrypt(String source) {
        generateKeyPair();
        try {
            if (rsaPublicKey==null) {
                /** 将文件中的公钥对象读出 */
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
                rsaPublicKey = (RSAPublicKey) ois.readObject();
                ois.close();
            }
            /** 得到Cipher对象来实现对源数据的RSA加密 */
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            // 模长
            int MaxBlockSize = KEYSIZE / 8;
            // 加密数据长度 <= 模长-11
            int len = (MaxBlockSize - 11) / 8;
            String[] datas = splitString(source, len);
            StringBuffer mi = new StringBuffer();
            for (String s : datas) {
                mi.append(bcd2Str(cipher.doFinal(s.getBytes())));
            }
            return mi.toString();
        }catch (Exception e){
            System.err.println("公钥加密方法:"+e);
        }
        return null;
    }
    /**
     * 私钥解密
     *
     * @param cryptograph  密文
     * @return             解密后的明文
     */
    public static String decrypt(String cryptograph) {
        generateKeyPair();
        try {
            if (rsaPrivateKey==null) {
                /** 将文件中的私钥对象读出 */
                @SuppressWarnings("resource")
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
                rsaPrivateKey = (RSAPrivateKey) ois.readObject();
                ois.close();
            }
            /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            // 模长
            int key_len = KEYSIZE / 8;
            byte[] bytes = cryptograph.getBytes();
            byte[] bcd = ASCII2BCD(bytes, bytes.length);
            StringBuffer sBuffer = new StringBuffer();
            byte[][] arrays = splitArray(bcd, key_len);
            for (byte[] arr : arrays) {
                sBuffer.append(new String(cipher.doFinal(arr)));
            }
            return sBuffer.toString();
        }catch (Exception e){
            System.err.println("私钥解密:"+e);
        }
        return null;
    }
    /**
     * 公钥加密
     *
     * @param data       数据源
     * @param publicKey  公钥
     * @return           加密后的密文
     */
    public static String encrypt(String data, RSAPublicKey publicKey) {
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 模长
            int key_len = publicKey.getModulus().bitLength() / 8;
            // 加密数据长度 <= 模长-11
            int len = (key_len - 11) / 8;
            String[] datas = splitString(data, len);
            String mi = "";
            //如果明文长度大于模长-11则要分组加密
            for (String s : datas) {
                mi += bcd2Str(cipher.doFinal(s.getBytes()));
            }
            return mi;
        }catch (Exception e){
            System.err.println("公钥加密方法:"+e);
        }
        return null;

    }
    /**
     * 私钥解密
     *
     * @param data         密文
     * @param privateKey   私钥
     * @return             解密后的明文
     */
    public static String decrypt(String data, RSAPrivateKey privateKey) {
        try{
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII2BCD(bytes, bytes.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for(byte[] arr : arrays){
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
        }catch (Exception e){
            System.err.println("私钥解密:"+e);
        }
        return null;
    }
    /**
     * 私钥加密
     *
     * @param data        数据源
     * @param privateKey  私钥
     * @return            加密后的密文
     */
    public static String encrypt(String data, RSAPrivateKey privateKey){
        try{
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        // 模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        int len = (key_len - 11) / 8;
        String[] datas = splitString(data, len);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
        }catch (Exception e){
            System.err.println("私钥加密:"+e);
        }
        return null;
    }
    /**
     * 公钥解密
     *
     * @param data        密文
     * @param publicKey   公钥
     * @return            解密后的明文
     */
    public static String decrypt(String data, RSAPublicKey publicKey){
        try{
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        //模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII2BCD(bytes, bytes.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for(byte[] arr : arrays){
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
        }catch (Exception e){
            System.err.println("公钥解密:"+e);
        }
        return null;
    }
    /**
     * 字符串分片
     *
     * @param string    源字符串
     * @param len       单片的长度（keysize/8）
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

    public static void main(String[] args) {
        String source = "{\"returnState\":true,\"data\":{\"message\":\"查询成功\",\"statue\":1,\"data\":[{\"id\":119,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013544_2NMT8YWAW0.jpg\"},{\"id\":120,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226013552_43YDAAMOFU.jpg\"},{\"id\":121,\"status\":1,\"IP\":\"http://yolowebimage.oss-cn-hangzhou.aliyuncs.com/iamge/\",\"imgurl\":\"20171226015552_LNF8PQUNYN.jpg\"}]}}";
        source=source+source;

        try {
            long star=System.currentTimeMillis();
            //生成公钥和私钥
            RSAPublicKey publicKey = (RSAPublicKey) getRSAPublickey();
            RSAPrivateKey privateKey = (RSAPrivateKey) getRSAPrivatekey();
            System.out.println("生成秘钥时间："+(System.currentTimeMillis()-star)+"ms");

            star=System.currentTimeMillis();
            String s = encrypt(source);
            System.out.println("公钥加密时间："+(System.currentTimeMillis()-star)+"ms");
            System.out.println("公钥加密："+s);

            star=System.currentTimeMillis();
            System.out.println("私钥解密："+decrypt(s));
            System.out.println("私钥解密时间："+(System.currentTimeMillis()-star)+"ms");

            String sign = sign(s.getBytes(), getprivatekey());
            System.err.println("签名:" + sign);
            boolean status = verify(s.getBytes(), getpublickey(), sign);
            System.err.println("验证结果:" + status);

            //模
            String modulus = publicKey.getModulus().toString();
            System.err.println("模:"+modulus);
            //公钥指数
            String public_exponent = publicKey.getPublicExponent().toString();
            System.err.println("公钥指数:"+public_exponent);
            //私钥指数
            String private_exponent = privateKey.getPrivateExponent().toString();
            System.err.println("私钥指数:"+private_exponent);

            star=System.currentTimeMillis();
            //使用模和指数生成公钥和私钥
            RSAPublicKey pubKey = getPublicKey(modulus, public_exponent);
            RSAPrivateKey priKey = getPrivateKey(modulus, private_exponent);
            System.out.println("模生成秘钥时间："+(System.currentTimeMillis()-star)+"ms");

            star=System.currentTimeMillis();
            //加密后的密文
            String  mi = encrypt(source, pubKey);
            System.out.println("公钥加密时间："+(System.currentTimeMillis()-star)+"ms");
            System.err.println("公钥加密后的密文:"+mi);

            star=System.currentTimeMillis();
            //解密后的明文
            String mings = decrypt(mi, priKey);
            System.out.println("私钥解密时间："+(System.currentTimeMillis()-star)+"ms");
            System.err.println("私钥解密后的明文:"+mings);

            String signs = sign(s.getBytes(), priKey);
            System.err.println("签名:" + signs);
            boolean statuss = verify(s.getBytes(), pubKey, signs);
            System.err.println("验证结果:" + statuss);

            star=System.currentTimeMillis();
            //加密后的密文
            String  mis = encrypt(source, priKey);
            System.out.println("私钥加密时间："+(System.currentTimeMillis()-star)+"ms");
            System.err.println("私钥加密后的密文:"+mis);

            star=System.currentTimeMillis();
            //解密后的明文
            String mingss = decrypt(mis, pubKey);
            System.out.println("公钥解密时间："+(System.currentTimeMillis()-star)+"ms");
            System.err.println("公钥解密后的明文:"+mingss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}