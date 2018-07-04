package com.base.MapUtil;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

import systemdb.PositionEntity;

/**
 * ClassName:RouteTask <br/>
 * Function: 封装的驾车路径规划 <br/>
 * Date: 2015年4月3日 下午2:38:10 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */

public class RouteTask implements OnRouteSearchListener {

	private RouteSearch mRouteSearch;

	private PositionEntity mFromPoint;

	private PositionEntity mToPoint;

	private List<OnRouteCalculateListener> mListeners = new ArrayList<OnRouteCalculateListener>();
	private static Context mContext;
	public interface OnRouteCalculateListener {
		public void onRouteCalculate(float cost, float distance, int duration);

	}
	/**
	 * 内部类，在装载该内部类时才会去创建单利对象
	 */
	private static class SingletonHolder{
		private static final RouteTask instance=new RouteTask(mContext) ;
	}
	public static RouteTask getInstance(Context context) {
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
	public PositionEntity getStartPoint() {
		return mFromPoint;
	}

	public void setStartPoint(PositionEntity fromPoint) {
		mFromPoint = fromPoint;
	}

	public PositionEntity getEndPoint() {
		return mToPoint;
	}

	public void setEndPoint(PositionEntity toPoint) {
		mToPoint = toPoint;
	}

	private RouteTask(Context context) {
		mRouteSearch = new RouteSearch(context);
		mRouteSearch.setRouteSearchListener(this);
	}

	public void search() {
		if (mFromPoint == null || mToPoint == null) {
			return;
		}

		FromAndTo fromAndTo = new FromAndTo(new LatLonPoint(mFromPoint.latitue,
				mFromPoint.longitude), new LatLonPoint(mToPoint.latitue,
				mToPoint.longitude));
		DriveRouteQuery driveRouteQuery = new DriveRouteQuery(fromAndTo,
				RouteSearch.DrivingDefault, null, null, "");

		mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
	}

	public void search(PositionEntity fromPoint, PositionEntity toPoint) {

		mFromPoint = fromPoint;
		mToPoint = toPoint;
		search();

	}

	public void addRouteCalculateListener(OnRouteCalculateListener listener) {
		synchronized (this) {
			if (mListeners.contains(listener))
				return;
			mListeners.add(listener);
		}
	}

	public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
		synchronized (this) {
			mListeners.remove(listener);
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult driveRouteResult,
									 int resultCode) {
		if (resultCode == 0 && driveRouteResult != null) {
			synchronized (this) {
				for (OnRouteCalculateListener listener : mListeners) {
					List<DrivePath> drivepaths = driveRouteResult.getPaths();
					float distance = 0;
					int duration = 0;
					if (drivepaths.size() > 0) {
						DrivePath drivepath = drivepaths.get(0);

						distance = drivepath.getDistance() / 1000;//·��

						duration = (int) (drivepath.getDuration() / 60);//ʱ��
					}

					float cost = driveRouteResult.getTaxiCost();//����

					listener.onRouteCalculate(cost, distance, duration);
				}

			}
		}
		// TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理

	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

	}
}
