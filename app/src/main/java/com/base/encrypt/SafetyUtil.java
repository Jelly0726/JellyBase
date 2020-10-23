package com.base.encrypt;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ObjectStreamException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * 加密签名工具类
 */
public class SafetyUtil {
	public static final int MD5=0;//MD5
	public static final int HMAC_SHA1=1;//HmacSHA1
	public static final int SHA1=2;//SHA1
	public static final int SHA224=3;//SHA224
	public static final int SHA256=4;//SHA256
	public static final int SHA384=5;//SHA384
	public static final int SHA512=6;//SHA512
	public static final int AES=7;//AES
	public static final int RSA_PUBKEY=8;//RSAPUBKEY
	public static final int RSA_PRIVATEKEY=9;//RSAPRIVATEKEY
	public static final int XOR=10;//XOR
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
		if (type== RSA_PUBKEY || type==RSA_PRIVATEKEY){//使用RSA验签只能是私钥加密，公钥验证
			type=RSA_PUBKEY;
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
		if (type== RSA_PUBKEY || type==RSA_PRIVATEKEY){//使用RSA验签只能是私钥加密，公钥验证
			type=RSA_PUBKEY;
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
					AES = jni.decodeByAES(context.getApplicationContext(), Base64.decode(source, Base64.NO_WRAP));
				}else {
					AES = jni.decodeByAES(context.getApplicationContext(),source.getBytes());
				}
				sign = new String(AES);
				break;
			case RSA_PUBKEY:
				byte[] RSA_PUBKEY;
				//字符串不是Base64 不需要Base64解码
				if (isBase64(source)){
					RSA_PUBKEY = jni.decodeByRSAPubKey(context.getApplicationContext(), Base64.decode(source, Base64.NO_WRAP));
				}else {
					RSA_PUBKEY = jni.decodeByRSAPubKey(context.getApplicationContext(),source.getBytes());
				}
				sign =  new String(RSA_PUBKEY);
				break;
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
				byte[] AES = jni.encodeByAES(context, source.getBytes());
				sign = Base64.encodeToString(AES,Base64.NO_WRAP);
				break;
			case RSA_PUBKEY:
				byte[] RSA_PUBKEY = jni.encodeByRSAPubKey(context, source.getBytes());
				sign = Base64.encodeToString(RSA_PUBKEY,Base64.NO_WRAP);
				break;
			case RSA_PRIVATEKEY:
				byte[] RSA_PRIVATEKEY = jni.encodeByRSAPrivateKey(context, source.getBytes());
				sign = Base64.encodeToString(RSA_PRIVATEKEY,Base64.NO_WRAP);
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
}
