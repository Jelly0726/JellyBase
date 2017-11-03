package com.base.MapUtil;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * ClassName:OnLocationGetListener <br/>
 * Function: 逆地理编码或者定位完成后回调接口<br/>
 * Date:     2015年4月2日 下午6:17:17 <br/>
 * @author   yiyi.qi
 * @version
 * @since    JDK 1.6
 * @see
 */
public interface OnLocationGetListener {
	/**
	 * 定位回调
	 * @param amapLocation
	 */
	public void onLocationGet(AMapLocation amapLocation);

	/**
	 * 逆地理编码（地址转坐标）
	 * @param regeocodeReult
	 */
	public void onRegecodeGet(RegeocodeResult regeocodeReult);
	/**
	 * 地理编码（坐标转地址）
	 * @param geocodeReult
	 */
	public void onGecodeGet(GeocodeResult geocodeReult);
}
