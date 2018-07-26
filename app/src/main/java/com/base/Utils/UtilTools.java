package com.base.Utils;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.base.config.BaseConfig;
import com.base.encrypt.MD5;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;

public class UtilTools {
	/**
	 * 获取一个网络连接客户端
	 */
	private static OkHttpClient mOkHttpClient;
	public static OkHttpClient getClient(){
		if(mOkHttpClient==null){
			mOkHttpClient= new OkHttpClient();
		}
		return mOkHttpClient;
	}
	public static String getJson(final String url){
		String list = null;
		try{
			final ExecutorService exec= Executors.newFixedThreadPool(3);
			Callable<String> call=new Callable<String>(){
				public String call() throws Exception {
					String string = null;
					String address = url;
					URL url = new URL(address);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setUseCaches(false);
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream in = connection.getInputStream();
						// 将流转化为字符串
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in));
						String tmpString = "";
						StringBuilder retJSON = new StringBuilder();
						while ((tmpString = reader.readLine()) != null) {
							retJSON.append(tmpString + "\n");
						}
						string=retJSON.toString();
						return string;
					} else {
						//Log.e("WF", "网络连接异常！获取数据失败");
					}
					return "";
				}
			};
			Future<String> task=exec.submit(call);
			list=task.get();
			exec.shutdown();
			return list;
		}catch (Exception e){
			//Log.e("WF", "获取数据失败，异常信息是：" + e.toString());
		}
		return list;
	}
	public static String reTimeT(String timeT){
		String time="00-00-00 00:00:00";
		try{
			if(timeT!=null) {
				time = timeT.replace("T", " ");
			}else{
				return time;
			}
		}catch (Exception e){
			return "00-00-00 00:00:00";
		}
		return time;
	}
	/***
	 * 获取当前时间
	 * @return
	 */
	public static String getCurentDate(){
		String date, day;
		int today;
		Calendar calendar = Calendar.getInstance();
		today = calendar.get(Calendar.DAY_OF_WEEK);
		switch (today){
			case 2:
				day = "星期一";
				break;
			case 3:
				day = "星期二";
				break;
			case 4:
				day = "星期三";
				break;
			case 5:
				day = "星期四";
				break;
			case 6:
				day = "星期五";
				break;
			case 7:
				day = "星期六";
				break;
			case 8:
				day = "星期天";
				break;
			default:
				day = "日期错误";
				break;
		}
		date = calendar.get(Calendar.MONTH) + 1 + "月" + calendar.get(Calendar.DATE) + "日  " + day;
		return date;
	}
	/***
	 * "x,y"解析出x    y
	 * @param location
	 * @return
	 */
	public static LatLng getLocationPoints(String location){
		float x = 0, y = 0;
		String xy[] = location.split(",");
		try{
			x = Float.parseFloat(xy[0]);
			y = Float.parseFloat(xy[1]);
		}catch (Exception e){
			e.printStackTrace();
		}
		return new LatLng(x,y);
	}

	/**
	 *
	 * 获取签名字符串
	 * @param map  参数
	 * @param type  0 无随机 1随机
	 * @return
	 */
	public static Map<String, String> getSign(Map<String, String> map, int type){
		if(type==1) {
			String uuid = UUID.randomUUID().toString()
					.replaceAll("-", "").substring(0, 20).toUpperCase();
			map.put("nonce_str",uuid);
		}
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
		stringBuffer.append("&key=").append(BaseConfig.KEY);
		//Log.i("msg","签名前="+stringBuffer.toString().toLowerCase());
		map.put("sign", MD5.MD5Encode(stringBuffer.toString().toLowerCase()).toUpperCase());
		return map;
		//return MD5(stringBuffer.toString()).toUpperCase();
	}
	/**
	 * 使用 Map按key进行排序
	 * @param map
	 * @return
	 */
	public static Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, String> sortMap = new TreeMap<String, String>(
				new MapKeyComparator());

		sortMap.putAll(map);
		return sortMap;
	}
	//比较器类

	private static class MapKeyComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {

			return str1.compareTo(str2);
		}
	}
}
