package com.base.encrypt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
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
	public static final int XOR=12;//XOR
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
		if (type== RSA_PUBKEY || type==RSA_PRIVATEKEY||type== RSA_VERIFY){//RSA签名验证只能是私钥加密公钥验证
			//字符串不是Base64 不需要Base64解码
			if (isBase64(source)){
				return jni.verifyByRSAPubKey(context.getApplicationContext(), source.getBytes(),Base64.decode(sign, Base64.NO_WRAP)) ==1;
			}else {
				return jni.verifyByRSAPubKey(context.getApplicationContext(),source.getBytes(),sign.getBytes()) ==1;
			}

		}
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
		if (type== RSA_PUBKEY || type==RSA_PRIVATEKEY||type== RSA_VERIFY){//RSA签名验证只能是私钥加密公钥验证
			//字符串不是Base64 不需要Base64解码
			if (isBase64(stringBuffer.toString())){
				return jni.verifyByRSAPubKey(context.getApplicationContext(), stringBuffer.toString().getBytes()
						,Base64.decode(sign, Base64.NO_WRAP)) ==1;
			}else {
				return jni.verifyByRSAPubKey(context.getApplicationContext(),stringBuffer.toString().getBytes()
						,sign.getBytes()) ==1;
			}

		}
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
	 * @param publicKey  公钥base64字符串（没换行）
	 * @param source  待加密的明文
	 * @return
	 */
	public String encryptRSA(Context context,@NonNull String publicKey,@NonNull String source){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "签名加密前:"+source);
		String sign=jni.encryptRSA(context.getApplicationContext(),publicKey,source);
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
	public String encryptRSA(Context context,@NonNull String publicKey,@NonNull Map<String, String> source){
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
		String sign=jni.encryptRSA(context.getApplicationContext(),publicKey,stringBuffer.toString());
		Log.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * RSA私钥解密
	 * @param privateKey  私钥base64字符串（没换行）
	 * @param source      待解密的base64密文
	 * @return
	 */
	public String decryptRSA(Context context,@NonNull String privateKey,@NonNull String source){
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		Log.i("SafetyUtil", "解密前:"+source);
		String sign=jni.decryptRSA(context.getApplicationContext(),privateKey,source);
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
		byte[] su=jni.encryptByAESEncrypt(context.getApplicationContext(),source.getBytes(), key.getBytes());
		String sign=Base64.encodeToString(su,Base64.NO_WRAP);
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
		String sign=new String(jni.decryptByAESEncrypt(context.getApplicationContext(),Base64.decode(source,Base64.NO_WRAP), key.getBytes()));
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
		byte[] su=jni.encryptAESCipher(context.getApplicationContext(),source.getBytes(), key.getBytes());
		String sign=Base64.encodeToString(su,Base64.NO_WRAP);
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
		String sign=new String(jni.decryptAESCipher(context.getApplicationContext(),Base64.decode(source,Base64.NO_WRAP), key.getBytes()));
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
				byte[] AES;
//				字符串不是Base64 不需要Base64解码
				if (isBase64(source)){
					AES = jni.decodeByAESEncrypt(context.getApplicationContext(), Base64.decode(source, Base64.NO_WRAP));
				}else {
					AES = jni.decodeByAESEncrypt(context.getApplicationContext(),source.getBytes());
				}
				sign = new String(AES);
				break;
			case RSA_PUBKEY://RSA解密都是使用私钥解密
			case RSA_PRIVATEKEY:
				byte[] RSA_PRIVATEKEY;
				//字符串不是Base64 不需要Base64解码
				if (isBase64(source)){
					RSA_PRIVATEKEY = jni.decodeByRSAPrivateKey(context.getApplicationContext(), Base64.decode(source, Base64.NO_WRAP));
				}else {
					RSA_PRIVATEKEY = jni.decodeByRSAPrivateKey(context.getApplicationContext(),source.getBytes());
				}
				sign = new String(RSA_PRIVATEKEY);
				break;
			case XOR:
				byte[] XOR;
				//字符串不是Base64 不需要Base64解码
				if (isBase64(source)){
					XOR = jni.xOr(context.getApplicationContext(), Base64.decode(source, Base64.NO_WRAP));
				}else {
					XOR = jni.xOr(context.getApplicationContext(),source.getBytes());
				}
				sign =  new String(XOR);
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
				sign = jni.md5(context, source.getBytes());
				break;
			case HMAC_SHA1:
				byte[] HMAC = jni.encodeByHmacSHA1(context, source.getBytes());
				sign =  Base64.encodeToString(HMAC,Base64.NO_WRAP);
				break;
			case SHA1:
				sign = jni.encodeBySHA1(context, source.getBytes());
				break;
			case SHA224:
				sign = jni.encodeBySHA224(context, source.getBytes());
				break;
			case SHA256:
				sign = jni.encodeBySHA256(context, source.getBytes());
				break;
			case SHA384:
				sign = jni.encodeBySHA384(context, source.getBytes());
				break;
			case SHA512:
				sign = jni.encodeBySHA512(context, source.getBytes());
				break;
			case AES:
				byte[] AES = jni.encodeByAESEncrypt(context, source.getBytes());
				sign = Base64.encodeToString(AES,Base64.NO_WRAP);
				break;
			case RSA_PRIVATEKEY://RSA加密签名都是使用公钥加密
			case RSA_PUBKEY:
				byte[] RSA_PUBKEY = jni.encodeByRSAPubKey(context, source.getBytes());
				sign = Base64.encodeToString(RSA_PUBKEY,Base64.NO_WRAP);
//				sign=new String(RSA_PUBKEY);
				break;
			case RSA_SIGN://rsa签名是用私钥签名
				byte[] RSA_PRIKEY = jni.signByRSAPrivateKey(context, source.getBytes());
				sign = Base64.encodeToString(RSA_PRIKEY,Base64.NO_WRAP);
				break;
			case XOR:
				byte[] XOR = jni.xOr(context, source.getBytes());
				sign = Base64.encodeToString(XOR,Base64.NO_WRAP);
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
