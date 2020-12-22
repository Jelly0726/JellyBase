package com.base.encrypt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ObjectStreamException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * 加密签名工具类
 */
public class SafetyUtil {
//	public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJSLLZmJ6F3gznNJiC/ZNXA6Bmn0m1YgnsvaUnZRsGOOso01ydFDwLePI35jnS0/RKAtP7GQJK0DAr/o6KPzCpZAL6zFaviYb5GBthrqox8RikDr+VvzG5KN+Z9UD2pzEtmHU0iFc3PT66oAAu4Ss9qVDoWOoOUAUiiI4k+MMEU1AgMBAAECgYAhKzrRcBPs8ofnAmJgnNXr62kHO9F71+jdiDClrvP+Jx0DnyEjk0dzNYktbbzpH5mJUtFIKvGlmGiCxdU81sZkERmiJ0pGf/MszFjYiFwvzZYwHRW2swOG+cYsJhlZjAnn1xcTX9thB4siZ53/k7tvR9YAOwhyLIC/qD89+exAOQJBAPYGnxMv9dOVmlJK5wiMQnGI7ZT3+xa8ddH62JyHukFvb1LVncOa8SQubUD5eoJoJ32eBxbyt4Y3DFJxI10wS/sCQQCakNaKX2+cD+/8e+BHMG85fk3t+jW1iC/rYe5IUKjtY2QHqBArEtF+/9p9UwIbAGPJ67eVTXPRu3PxU7WphTyPAkEAkrHuBf3R4UBRzQG2ckVXlOTlbK7UO4FR60tb/zF64Gt2gHi44hov8LfyEwzufHVoHqGsboV44oFOSpYFVRpoIwJAXa/lGsJ2ODZA1N2ROBVXlZXFTrYW0A3YXehiMlsRybIw86MfCbzCVyRmHwitgghedAn4oPrtdPcWc/S1bCdiaQJAbjcE5f8fDosRrV8A3KWkaflaJHahxPdqPgVeHNNpziy2ZjJ9N9X23GlPy57XtheVX8EBrxO377xIxz9tYsJQyw==";
//	public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoWPzLsJq9/aozb4EJKytHNT07nFjDTUrP/8dlYyzI0KqFqeiYi9f3e96USm0bOjR4TJkzSq8QN92Maej/2VmgrZqu+6e47eXb6/nWIzaDh+Ae06YguUJij+yfIAagLiGAv2kHXz+w10wiht5Jt1H9gIEtS3+jwf5P/a0tQhBgiQIDAQAB";
	public static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMIICXAIBAAKBgQC81XqEKdhAIG5dSeps6tWNuXcPrjpBvzGsls7ljWNPEzwJrmWf\nEO6iRUYxwZXyQ+28e5BNHdlfJpS8L6wB4NV/ISysXH5k0qAJYDEP4jeRShi38SFT\nqMG95CTNJFKYMHejZNMMoyYvVID/p03JRXjoc9EVcQP0k+SeI9zFcqjsJQIDAQAB\nAoGATiISwI7D4LzKjaUg75I3bJ3Z6s4PYtbmieAYmZjoB3cQ93yGpcuOweviAIJ2\nNbjvrHaAHbiFEb7X+gnLpTdPfsR4jWFvAvhUFLu9zQwXRuOS2PSYGNtJzOXyh8p7\nZ74IR8i7I1hb1IhDsENL3aUadvOQC762D34t9IRohGmac0ECQQD4uG5AeZAcVvQg\nvO2/FpQy99lkoThTRhvIH2yvVDcKV7HRRv5/f+rvppqeM4z0nObtjzBGyNVN65VL\nQLLWNDbRAkEAwlxXKjRQLDKfkDjeD/FLiJGc85m0kdSPyaXg4IKv3qZl5L8GL4O+\neBdFnhLQ3RHavghGxflRxzNXpxCxm67dFQJADR6lai8/Y89OZ1+v5tGJFbsvM3ix\noOrk0kSeFg2KLbh8f76P9CfKO8P9CfVMLScNo2BXOpSjc83GfUa3aEcu0QJAJCCx\n+yBaPrzyOAa6EFCT78DRYd6SWAEg8SSqVlE0i7h2fDyd07szbnM095sbw9wLwwMa\n1LXxY4vBoUZTHVM1uQJBALgJq2Cr4KSQMS5QS4uaq6oQIfJiBqDU7abL0HItnonm\nT6EfzsK/Hp2t9ieiMQ4SZQ0xZYlZEc9qxTqny0ryUSw=\n-----END RSA PRIVATE KEY-----\n";
	public static final String PUBLIC_KEY = "-----BEGIN RSA PUBLIC KEY-----\nMIGJAoGBALzVeoQp2EAgbl1J6mzq1Y25dw+uOkG/MayWzuWNY08TPAmuZZ8Q7qJF\nRjHBlfJD7bx7kE0d2V8mlLwvrAHg1X8hLKxcfmTSoAlgMQ/iN5FKGLfxIVOowb3k\nJM0kUpgwd6Nk0wyjJi9UgP+nTclFeOhz0RVxA/ST5J4j3MVyqOwlAgMBAAE=\n-----END RSA PUBLIC KEY-----\n";
	private static final String BASE_RAMDOM_STRING="abcdefghijklmnopqrstuvwxyz0123456789";
	public static final int MD5=0;//MD5
	public static final int HMAC_SHA1=1;//HmacSHA1
	public static final int SHA1=2;//SHA1
	public static final int SHA224=3;//SHA224
	public static final int SHA256=4;//SHA256
	public static final int SHA384=5;//SHA384
	public static final int SHA512=6;//SHA512
	public static final int AES=7;//AES
	public static final int RSA_PUBKEY=8;//公钥加密
	public static final int RSA_PRIVATEKEY=9;//私钥解密
	public static final int RSA_SIGN=10;//RSA签名
	public static final int RSA_VERIFY=11;//RSA验证签名
	private static int count = 0;
	private JniUtils jni= new JniUtils();
	/**
	 * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
	 * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
	 * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
	 */
	private SafetyUtil() {
		/**
		 * 通过反射获得单例类的构造函数
		 * 抵御这种攻击，要防止构造函数被成功调用两次。需要在构造函数中对实例化次数进行统计，大于一次就抛出异常。
		 */
		synchronized (SafetyUtil.class) {
			if(count > 0){
				throw new RuntimeException("创建了两个实例");
			}
			count++;
		}
	}
	/**
	 * 内部类，在装载该内部类时才会去创建单利对象
	 */
	private static class SingletonHolder{
		private static final SafetyUtil instance = new SafetyUtil();
	}
	/**
	 * 单一实例
	 */
	public static SafetyUtil getInstance() {
		return SingletonHolder.instance;
	}
	/**
	 * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		return SingletonHolder.instance;
	}
	/**
	 * 获取RSA秘钥对
	 * @param context
	 * @return
	 */
	public String[] generateRSAKey(Context context){
		String[] PublicRSAKey=jni.generateRSAKey(context);
		return PublicRSAKey;
	}
	/**
	 *
	 * 签名验证
	 * @param source  源字符串
	 * @param sign    签名
	 * @param type  签名验证算法
	 * @return
	 */
	public boolean verify(Context context, @NonNull String source, @NonNull String sign, int type){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
		String signs=sign(context.getApplicationContext(),source,type);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return signs.equals(sign);
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * 签名验证
	 * @param source  源字符串
	 * @param sign    签名
	 * @param type  签名验证算法
	 * @return
	 */
	public boolean verify(Context context,@NonNull Map<String, String> source,@NonNull String sign, int type){
		source.put("timestamp", System.currentTimeMillis()+"");//时间戳
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= String.valueOf((Object) entent.getValue());
			if(!TextUtils.isEmpty(value)) {
				stringBuffer
						.append(key)
						.append("=")
						.append(value);
				if (iterator.hasNext()) {
					stringBuffer.append("&");
				}
			}
		}
		if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
		}
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String signs=sign(context.getApplicationContext(),stringBuffer.toString(),type);
		Log.i("SafetyUtil", "签名加密后:"+signs);
		return signs.equals(sign);
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * 加密字符串
	 * @param source  源字符串
	 * @param type  加密签名算法
	 * @return
	 */
	public String encode(Context context,@NonNull String source, int type){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
		String sign=sign(context.getApplicationContext(),source, type);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * 加密字符串
	 * @param source  源字符串
	 * @param type  加密签名算法
	 * @return
	 */
	public String encode(Context context,@NonNull Map<String, String> source, int type){
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= String.valueOf((Object) entent.getValue());
			if(!TextUtils.isEmpty(value)) {
				stringBuffer
						.append(key)
						.append("=")
						.append(value);
				if (iterator.hasNext()) {
					stringBuffer.append("&");
				}
			}
		}
		if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
		}
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String sign=sign(context.getApplicationContext(),stringBuffer.toString(), type);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}


	/**
	 * RSA公钥加密
	 * 加密字符串
	 * @param source  待加密的明文
	 * @param publicKey  公钥base64字符串（没换行）
	 * @return
	 */
	public String encryptRSA(Context context,@NonNull String source,@NonNull String publicKey){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
		String sign=jni.encryptRSA(context.getApplicationContext(),source,publicKey);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * RSA公钥加密
	 * 加密字符串
	 * @param publicKey  公钥base64字符串（没换行）
	 * @param source  	  待加密的明文
	 * @return
	 */
	public String encryptRSA(Context context,@NonNull Map<String, String> source,@NonNull String publicKey){
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= String.valueOf((Object) entent.getValue());
			if(!TextUtils.isEmpty(value)) {
				stringBuffer
						.append(key)
						.append("=")
						.append(value);
				if (iterator.hasNext()) {
					stringBuffer.append("&");
				}
			}
		}
		if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
		}
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String sign=jni.encryptRSA(context.getApplicationContext(),stringBuffer.toString(),publicKey);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * RSA私钥解密
	 * @param source      待解密的base64密文
	 * @param privateKey  私钥base64字符串（没换行）
	 * @return
	 */
	public String decryptRSA(Context context,@NonNull String source,@NonNull String privateKey){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "解密前:"+source);
		String sign=jni.decodeByRSAPubKey(context.getApplicationContext(),source,privateKey);
		Log.i("SafetyUtil", "解密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * RSA私钥加密
	 * 加密字符串
	 * @param source  待加密的明文
	 * @param privateKey  私钥base64字符串（没换行）
	 * @return
	 */
	public String encodeByRSAPriKey(Context context,@NonNull String source,@NonNull String privateKey){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
		String sign=jni.encodeByRSAPriKey(context.getApplicationContext(),source,privateKey);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * RSA私钥加密
	 * 加密字符串
	 * @param privateKey  私钥base64字符串（没换行）
	 * @param source  	  待加密的明文
	 * @return
	 */
	public String encodeByRSAPriKey(Context context,@NonNull Map<String, String> source,@NonNull String privateKey){
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= String.valueOf((Object) entent.getValue());
			if(!TextUtils.isEmpty(value)) {
				stringBuffer
						.append(key)
						.append("=")
						.append(value);
				if (iterator.hasNext()) {
					stringBuffer.append("&");
				}
			}
		}
		if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
		}
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String sign=jni.encodeByRSAPriKey(context.getApplicationContext(),stringBuffer.toString(),privateKey);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * RSA公钥解密
	 * @param source      待解密的base64密文
	 * @param publicKey  公钥base64字符串（没换行）
	 * @return
	 */
	public String decodeByRSAPubKey(Context context,@NonNull String source,@NonNull String publicKey){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "解密前:"+source);
		String sign=jni.decodeByRSAPubKey(context.getApplicationContext(),source,publicKey);
		Log.i("SafetyUtil", "解密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * AES加密字符串
	 * @param source  源字符串
	 * @param key
	 * @return
	 */
	public String encryptByAESEncrypt(Context context,@NonNull String source,@NonNull String key){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
//		byte[] su=jni.encryptByAESEncrypt(context.getApplicationContext(),source.getBytes(), key.getBytes());
//		String sign=Base64.encodeToString(su,Base64.NO_WRAP);
		String sign=jni.encryptByAESEncrypt(context.getApplicationContext(),source.getBytes(), key.getBytes());
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * AES加密字符串
	 * @param keys
	 * @param source  	  待加密的明文
	 * @return
	 */
	public String encryptByAESEncrypt(Context context,@NonNull Map<String, String> source,@NonNull String keys){
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= String.valueOf((Object) entent.getValue());
			if(!TextUtils.isEmpty(value)) {
				stringBuffer
						.append(key)
						.append("=")
						.append(value);
				if (iterator.hasNext()) {
					stringBuffer.append("&");
				}
			}
		}
		if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
		}
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String sign=encryptByAESEncrypt(context,stringBuffer.toString(),keys);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * AES解密
	 * @param source  源字符串
	 * @param key
	 * @return
	 */
	public String decryptByAESEncrypt(Context context,@NonNull String source,@NonNull String key){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "解密前:"+source);
		//AESEncrypt
//		String sign=new String(jni.decryptByAESEncrypt(context.getApplicationContext(),Base64.decode(source,Base64.NO_WRAP), key.getBytes()));
		String sign=jni.decryptByAESEncrypt(context.getApplicationContext(),source.getBytes(), key.getBytes());
		Log.i("SafetyUtil", "解密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * AES加密字符串
	 * @param source  源字符串
	 * @param key
	 * @return
	 */
	public String encryptByAESCipher(Context context,@NonNull String source,@NonNull String key){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
//		byte[] su=jni.encryptByAESEncrypt(context.getApplicationContext(),source.getBytes(), key.getBytes());
//		String sign=Base64.encodeToString(su,Base64.NO_WRAP);
		String sign=jni.encryptAESCipher(context.getApplicationContext(),source.getBytes(), key.getBytes());
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * AES加密字符串
	 * @param keys
	 * @param source  	  待加密的明文
	 * @return
	 */
	public String encryptByAESCipher(Context context,@NonNull Map<String, String> source,@NonNull String keys){
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= String.valueOf((Object) entent.getValue());
			if(!TextUtils.isEmpty(value)) {
				stringBuffer
						.append(key)
						.append("=")
						.append(value);
				if (iterator.hasNext()) {
					stringBuffer.append("&");
				}
			}
		}
		if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
		}
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String sign=encryptByAESCipher(context,stringBuffer.toString(),keys);
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 *
	 * AES解密
	 * @param source  源字符串
	 * @param key
	 * @return
	 */
	public String decryptAESCipher(Context context,@NonNull String source,@NonNull String key){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "解密前:"+source);
		//AESEncrypt
//		String sign=new String(jni.decryptByAESEncrypt(context.getApplicationContext(),Base64.decode(source,Base64.NO_WRAP), key.getBytes()));
		String sign=jni.decryptAESCipher(context.getApplicationContext(),source.getBytes(), key.getBytes());
		Log.i("SafetyUtil", "解密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * 字符串解密
	 * @param source  源字符串(Base64)
	 * @param type  解密签名算法
	 * @return
	 */
	public String decode(Context context,@NonNull String source, int type){
		String sign;
		switch (type) {
			case AES:
				sign = jni.decodeByAESEncrypt(context.getApplicationContext(),source.getBytes());
				break;
			case RSA_PUBKEY://RSA公钥解密
				sign = jni.decodeByRSAPubKey(context.getApplicationContext(),source);
				break;
			case RSA_PRIVATEKEY://RSA私钥解密
				sign = jni.decodeByRSAPriKey(context.getApplicationContext(),source);
				break;
			default:
				sign = "";
				break;
		}
		return sign;
	}
	/**
	 * 使用 Map按key进行排序
	 * @param map
	 * @return
	 */
	public Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, String> sortMap = new TreeMap<String, String>(
				new MapKeyComparator());

		sortMap.putAll(map);
		return sortMap;
	}
	private String sign(Context context,@NonNull String source, int type){
		String sign;
		switch (type) {
			case MD5:
				sign = jni.md5(context, source);
				break;
			case HMAC_SHA1:
				sign= jni.encodeByHmacSHA1(context, source);
				break;
			case SHA1:
				sign = jni.encodeBySHA1(context, source);
				break;
			case SHA224:
				sign = jni.encodeBySHA224(context, source);
				break;
			case SHA256:
				sign = jni.encodeBySHA256(context, source);
				break;
			case SHA384:
				sign = jni.encodeBySHA384(context, source);
				break;
			case SHA512:
				sign = jni.encodeBySHA512(context, source);
				break;
			case AES:
				sign = jni.encodeByAESEncrypt(context, source.getBytes());
				break;
			case RSA_PRIVATEKEY://RSA私钥加密
				sign = jni.encodeByRSAPriKey(context, source);
				break;
			case RSA_PUBKEY://RSA公钥加密
				sign = jni.encodeByRSAPubKey(context, source);
//				sign=new String(RSA_PUBKEY);
				break;
			default:
				sign = "";
				break;
		}
		return sign;
	}
	//比较器类

	private class MapKeyComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {

			return str1.compareTo(str2);
		}
	}
	/**
	 * 使用正则判断字符串是否Base64
	 * @param str
	 * @return   true 是Base64 false 不是Base64
	 */
	private boolean isBase64(String str) {
		String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		return Pattern.matches(base64Pattern, str);
	}
	/**
	 *
	 * @Title: getAesRandomKeyString
	 * @author：liuyx
	 * @date：2016年5月10日上午9:30:15
	 * @Description: 获取AES随机密钥字符串
	 * @return
	 */
	public String getAESRandomKeyString(int length) {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(BASE_RAMDOM_STRING.length());
			sb.append(BASE_RAMDOM_STRING.charAt(number));
		}
		return sb.toString();
	}
}
