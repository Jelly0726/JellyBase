package com.base.encrypt;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.base.appManager.BaseApplication;
import com.base.log.DebugLog;

import java.io.ObjectStreamException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

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
	private JniUtils jni= new JniUtils();
	/**
	 * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
	 * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
	 * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
	 */
	private SafetyUtil() {}
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
	public boolean verify(@NonNull String source,@NonNull String sign, int type){
		source +="&key=";
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		DebugLog.i("SafetyUtil", "签名加密前:"+source);
		if (type== RSA_PUBKEY || type==RSA_PRIVATEKEY){//使用RSA验签只能是私钥加密，公钥验证
			type=RSA_PUBKEY;
		}
		String signs=sign(source,type);
		DebugLog.i("SafetyUtil", "签名加密后:"+sign);
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
	public boolean verify(@NonNull Map<String, String> source,@NonNull String sign, int type){
		source.put("timestamp", System.currentTimeMillis()+"");//时间戳
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= (String) entent.getValue();
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
		stringBuffer.append("&key=");
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		DebugLog.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		if (type== RSA_PUBKEY || type==RSA_PRIVATEKEY){//使用RSA验签只能是私钥加密，公钥验证
			type=RSA_PUBKEY;
		}
		String signs=sign(stringBuffer.toString(),type);
		DebugLog.i("SafetyUtil", "签名加密后:"+signs);
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
	public String encode(@NonNull String source, int type){
		source +="&key=";
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		DebugLog.i("SafetyUtil", "签名加密前:"+source);
		String sign=sign(source, type);
		DebugLog.i("SafetyUtil", "签名加密后:"+sign);
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
	public String encode(@NonNull Map<String, String> source, int type){
		source.put("timestamp", System.currentTimeMillis()+"");//时间戳
		source=sortMapByKey(source);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=source.entrySet().iterator();
		while(iterator.hasNext()){
			LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
			String key= (String) entent.getKey();
			String value= (String) entent.getValue();
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
		stringBuffer.append("&key=");
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
//		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
//		return map;
		DebugLog.i("SafetyUtil", "签名加密前:"+stringBuffer.toString());
		String sign=sign(stringBuffer.toString(), type);
		DebugLog.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}

	/**
	 * 字符串解密
	 * @param source  源字符串(Base64)
	 * @param type  解密签名算法
	 * @return
	 */
	public String decode(@NonNull String source, int type){
		String sign;
		switch (type) {
			case AES:
				byte[] AES = jni.decodeByAES(BaseApplication.getInstance(), Base64.decode(source, Base64.NO_WRAP));
				sign = Base64.encodeToString(AES,Base64.NO_WRAP);
				break;
			case RSA_PUBKEY:
				byte[] RSA_PUBKEY = jni.decodeByRSAPubKey(BaseApplication.getInstance(), Base64.decode(source, Base64.NO_WRAP));
				sign =  Base64.encodeToString(RSA_PUBKEY,Base64.NO_WRAP);
				break;
			case RSA_PRIVATEKEY:
				byte[] RSA_PRIVATEKEY = jni.decodeByRSAPrivateKey(BaseApplication.getInstance(), Base64.decode(source, Base64.NO_WRAP));
				sign = Base64.encodeToString(RSA_PRIVATEKEY,Base64.NO_WRAP);
				break;
			case XOR:
				byte[] XOR = jni.xOr(BaseApplication.getInstance(), Base64.decode(source, Base64.NO_WRAP));
				sign = Base64.encodeToString(XOR,Base64.NO_WRAP);
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
	private String sign(@NonNull String source, int type){
		String sign;
		switch (type) {
			case MD5:
				sign = jni.md5(BaseApplication.getInstance(), source.getBytes());
				break;
			case HMAC_SHA1:
				byte[] HMAC = jni.encodeByHmacSHA1(BaseApplication.getInstance(), source.getBytes());
				sign =  Base64.encodeToString(HMAC,Base64.NO_WRAP);
				break;
			case SHA1:
				sign = jni.encodeBySHA1(BaseApplication.getInstance(), source.getBytes());
				break;
			case SHA224:
				sign = jni.encodeBySHA224(BaseApplication.getInstance(), source.getBytes());
				break;
			case SHA256:
				sign = jni.encodeBySHA256(BaseApplication.getInstance(), source.getBytes());
				break;
			case SHA384:
				sign = jni.encodeBySHA384(BaseApplication.getInstance(), source.getBytes());
				break;
			case SHA512:
				sign = jni.encodeBySHA512(BaseApplication.getInstance(), source.getBytes());
				break;
			case AES:
				byte[] AES = jni.encodeByAES(BaseApplication.getInstance(), source.getBytes());
				sign = Base64.encodeToString(AES,Base64.NO_WRAP);
				break;
			case RSA_PUBKEY:
				byte[] RSA_PUBKEY = jni.encodeByRSAPubKey(BaseApplication.getInstance(), source.getBytes());
				sign = Base64.encodeToString(RSA_PUBKEY,Base64.NO_WRAP);
				break;
			case RSA_PRIVATEKEY:
				byte[] RSA_PRIVATEKEY = jni.encodeByRSAPrivateKey(BaseApplication.getInstance(), source.getBytes());
				sign = Base64.encodeToString(RSA_PRIVATEKEY,Base64.NO_WRAP);
				break;
			case XOR:
				byte[] XOR = jni.xOr(BaseApplication.getInstance(), source.getBytes());
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
}
