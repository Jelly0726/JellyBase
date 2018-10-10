package com.base.encrypt;

import android.text.TextUtils;

import com.base.appManager.BaseApplication;
import com.base.log.DebugLog;

import java.io.ObjectStreamException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 加密签名工具类
 */
public class SafetyUtil {
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
	 * 获取签名字符串
	 * @param map  参数
	 * @param type  0 无随机 1随机
	 * @return
	 */
	public String getSign(Map<String, String> map, int type){
		if(type==1) {
			String uuid = UUID.randomUUID().toString()
					.replaceAll("-", "").substring(0, 20).toUpperCase();
			map.put("nonce_str",uuid);
		}
        map.put("timestamp", System.currentTimeMillis()+"");//时间戳
		map=sortMapByKey(map);
		StringBuffer stringBuffer=new StringBuffer("");
		Iterator iterator=map.entrySet().iterator();
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
        String sign=jni.md5(BaseApplication.getInstance(),stringBuffer.toString().getBytes());
        DebugLog.i("SafetyUtil", "签名加密后:"+sign);
		return sign;
		//return MD5(stringBuffer.toString()).toUpperCase();
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
	//比较器类

	private class MapKeyComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {

			return str1.compareTo(str2);
		}
	}
}
