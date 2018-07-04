/**
 * Project Name:Android_Car_Example  
 * File Name:LocationTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月3日上午9:27:45
 *
 */
package com.base.MapUtil;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;


/**
 * ClassName:LocationTask <br/>
 * Function: 简单封装了定位请求，可以进行单次定位和多次定位，注意的是在app结束或定位结束时注意销毁定位 <br/>
 * Date: 2015年4月3日 上午9:27:45 <br/>
 *
 * @author yiyi.qi
 * @version
 * @see
 */
public class LocationTask implements  AMapLocationListener,
		OnLocationGetListener {

	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	private RegeocodeTask mRegecodeTask;
	private boolean isStar=false;
	private List<OnLocationGetListener> mListeners;
	private static Context mContext;
	private LocationTask(Context context) {
		//初始化定位
		mLocationClient = new AMapLocationClient(context);
		//设置定位回调监听
		mLocationClient.setLocationListener(this);
		mRegecodeTask = new RegeocodeTask(context);
		mRegecodeTask.setOnLocationGetListener(this);
		mListeners =new ArrayList<OnLocationGetListener>();
	}

	public void setOnLocationGetListener(
			OnLocationGetListener onGetLocationListener) {
		//mOnLocationGetlisGetListener = onGetLocationListener;
		synchronized (this) {
			if(mListeners.contains(onGetLocationListener))
				return;
			mListeners.add(onGetLocationListener);
		}
	}
	/**
	 * 内部类，在装载该内部类时才会去创建单利对象
	 */
	private static class SingletonHolder{
		private static final LocationTask instance =new LocationTask(mContext);
	}
	public static LocationTask getInstance(Context context) {
		mContext=context.getApplicationContext();
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
	 * 开启定位
	 * @param time  定位间隔  单位 毫秒  ms
	 * @param type  定位模式  0  高精度模式 1  为低功耗模式lbs 2 仅设备模式gps
	 */
	public void startLocate(int time,int type) {
//		// 对输入值进行判断,避免重复开启同样的定位
//		if (mTime == time && mType == type){
//			return;
//		}else {
//			mTime = time;
//			mType = type;
//		}
		if(mLocationClient!=null) {
			mLocationClient.stopLocation();//停止定位
		}
		//声明mLocationOption对象
		AMapLocationClientOption
				mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		if(type==0){//高精度模式
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		}else
		if(type==1){//低功耗模式lbs
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
		}else
		if(type==2){//仅设备模式gps
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
		}else{
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
		}

		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(time);
		//获取最近3s内精度最高的一次定位结果：
		//设置setOnceLocationLatest(boolean b)接口为true，
		// 启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，
		// setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
		mLocationOption.setOnceLocationLatest(false);
		//AMapLocationClientOption.setProtocol是静态方法。
		//AMapLocationClientOption.AMapLocationProtocol提供良种枚举：
		//HTTP代表使用http请求，HTTPS代表使用https请求。
		//单个定位客户端生命周期内（调用AMapLocationClient.onDestroy()方法结束生命周期）设置一次即可。
		//mLocationOption.setProtocol(AMapLocationProtocol.HTTP);
		//启用缓存策略，SDK将在设备位置未改变时返回之前相同位置的缓存结果。默认启用缓存策略
		mLocationOption.setLocationCacheEnable(false);

		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
		isStar=true;
		//PostDriverXY.postErrorMessage(MyApplication.mDriverId, "定位开启");
	}
	/**
	 * 结束定位，可以跟多次定位配合使用
	 */
	public void stopLocate() {
		if (mLocationClient!=null)
		mLocationClient.stopLocation();//停止定位
		isStar = false;
		//PostDriverXY.postErrorMessage(MyApplication.mDriverId, "定位停止");
	}
	public boolean getLocationStatus(){
		return isStar;
	}

	/**
	 * 销毁定位资源
	 */
	public void onDestroy() {
		try{
			if (mLocationClient != null) {
				mLocationClient.stopLocation();//停止定位
				mListeners.clear();
				mLocationClient.onDestroy();//销毁定位客户端
				mLocationClient=null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 移除监听
	 */
	public void RemoveListener(OnLocationGetListener listener){
		synchronized (this) {
			mListeners.remove(listener);
		}
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
			Log.i("WF","amapLocatio="+amapLocation.clone().getLatitude());
			for (OnLocationGetListener listener:mListeners) {
				((OnLocationGetListener) listener).onLocationGet(amapLocation);
			}
		}
	}

	/**
	 * 坐标变化回调
	 * @param amapLocation
	 */
	@Override
	public void onLocationGet(AMapLocation amapLocation) {

		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param regeocodeReult
	 */
	@Override
	public void onRegecodeGet(RegeocodeResult regeocodeReult) {

	}

	@Override
	public void onGecodeGet(GeocodeResult geocodeReult) {

	}

}
