/**
 * Project Name:Android_Car_Example  
 * File Name:RegeocodeTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月2日下午6:24:53
 *
 */

package com.base.MapUtil;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * ClassName:RegeocodeTask <br/>
 * Function: 简单的封装的逆地理编码功能 <br/>
 * Date: 2015年4月2日 下午6:24:53 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RegeocodeTask implements OnGeocodeSearchListener {
	private static final float SEARCH_RADIUS = 50;
	private OnLocationGetListener mOnLocationGetListener;
	private volatile  double latitude;
	private volatile double longitude;
	private GeocodeSearch mGeocodeSearch;

	public RegeocodeTask(Context context) {
		try {
			mGeocodeSearch = new GeocodeSearch(context);
		} catch (AMapException e) {
			throw new RuntimeException(e);
		}
		mGeocodeSearch.setOnGeocodeSearchListener(this);
	}

	public void search(double latitude, double longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
		RegeocodeQuery regecodeQuery = new RegeocodeQuery(new LatLonPoint(
				latitude, longitude), SEARCH_RADIUS, GeocodeSearch.AMAP);
		mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
	}

	public void setOnLocationGetListener(
			OnLocationGetListener onLocationGetListener) {
		mOnLocationGetListener = onLocationGetListener;
	}

	/**
	 * 地理编码是将中文地址(或地名描述)转换为地理坐标
	 * @param arg0
	 * @param arg1
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

	}

	/**
	 * 逆地理编码时将地理坐标转换为中文地址（地名描述）
	 * @param regeocodeReult
	 * @param resultCode
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult regeocodeReult,int resultCode) {
		if (resultCode == 1000) {
			if (regeocodeReult != null
					&& regeocodeReult.getRegeocodeAddress() != null
					&& mOnLocationGetListener != null) {
				mOnLocationGetListener.onRegecodeGet(regeocodeReult);
			}
		}
		//TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
	}

}
