package com.base.MapUtil;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.jelly.jellybase.R;

public class SimpleNaviActivity extends Activity implements AMapNaviListener,AMapNaviViewListener {

	private AMapNaviView mAMapNaviView;
	private AMapNavi mAMapNavi;
	private TTSController mTtsManager;
	private boolean isGps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.amap_basic_navi);
		mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
		mAMapNaviView.onCreate(savedInstanceState);
		mAMapNaviView.setAMapNaviViewListener(this);

		mTtsManager = TTSController.getInstance(getApplicationContext());
		mTtsManager.init();

		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.addAMapNaviListener(this);
		mAMapNavi.addAMapNaviListener(mTtsManager);

		isGps = getIntent().getBooleanExtra("gps", false);
		if (isGps)
			mAMapNavi.startNavi(NaviType.GPS);
		else {
			mAMapNavi.startNavi(NaviType.EMULATOR);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAMapNaviView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAMapNaviView.onPause();

		mTtsManager.stopSpeaking();
//        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
//        mAMapNavi.stopNavi();
	}

	@Override
	protected void onDestroy() {
		mAMapNaviView.onDestroy();
		mAMapNavi.stopNavi();
		super.onDestroy();
	}

	/**
	 * 导航初始化失败时的回调函数。
	 */
	@Override
	public void onInitNaviFailure() {

	}

	/**
	 * 导航初始化成功时的回调函数
	 */
	@Override
	public void onInitNaviSuccess() {

	}

	/**
	 * 启动导航后的回调函数
	 * @param i
	 */
	@Override
	public void onStartNavi(int i) {

	}

	/**
	 * 当前方路况光柱信息有更新时回调函数
	 */
	@Override
	public void onTrafficStatusUpdate() {

	}

	/**
	 * 当GPS位置有更新时的回调函数
	 * @param aMapNaviLocation
	 */
	@Override
	public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

	}

	/**
	 * 导航播报信息回调函数(过时)
	 * @param i
	 * @param s
	 */
	@Override
	public void onGetNavigationText(int i, String s) {

	}

	/**
	 * 导航播报信息回调函数。
	 * @param s
	 */
	@Override
	public void onGetNavigationText(String s) {

	}

	/**
	 * 模拟导航停止后回调函数。
	 */
	@Override
	public void onEndEmulatorNavi() {

	}

	/**
	 * 到达目的地后回调函数。
	 */
	@Override
	public void onArriveDestination() {

	}

	/**
	 * 步行或者驾车路径规划失败后的回调函数。
	 * @param i
	 */
	@Override
	public void onCalculateRouteFailure(int i) {

	}

	/**
	 * 偏航后准备重新规划的通知回调。
	 */
	@Override
	public void onReCalculateRouteForYaw() {

	}

	/**
	 * 驾车导航时，如果前方遇到拥堵时需要重新计算路径的回调。
	 */
	@Override
	public void onReCalculateRouteForTrafficJam() {

	}

	/**
	 * 驾车路径导航到达某个途经点的回调函数。
	 * @param i
	 */
	@Override
	public void onArrivedWayPoint(int i) {

	}

	/**
	 * 用户手机GPS设置是否开启的回调函数。
	 * @param b
	 */
	@Override
	public void onGpsOpenStatus(boolean b) {

	}

	/**
	 * 导航引导信息回调 naviinfo 是导航信息类。(过时)
	 * @param aMapNaviInfo
	 */
	@Override
	public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

	}

	/**
	 * 导航过程中的摄像头信息回调函数
	 * @param aMapCameraInfos
	 */
	@Override
	public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {

	}

	/**
	 * 服务区信息回调函数
	 * @param amapServiceAreaInfos
	 */
	@Override
	public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

	}

	/**
	 * 导航引导信息回调 naviinfo 是导航信息类。
	 * @param naviInfo
	 */
	@Override
	public void onNaviInfoUpdate(NaviInfo naviInfo) {

	}

	/**
	 * 巡航模式（无路线规划）下，道路设施信息更新回调(过时)
	 * @param trafficFacilityInfo
	 */
	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

	}
	/**
	 * 巡航模式（无路线规划）下，道路设施信息更新回调(过时)
	 * @param aMapNaviTrafficFacilityInfo
	 */
	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

	}

	/**
	 * 显示路口放大图回调(实景图)。
	 * @param aMapNaviCross
	 */
	@Override
	public void showCross(AMapNaviCross aMapNaviCross) {

	}

	/**
	 * 关闭路口放大图回调(实景图)。
	 */
	@Override
	public void hideCross() {

	}

	@Override
	public void showModeCross(AMapModelCross aMapModelCross) {

	}

	/**
	 * 关闭路口放大图回调(模型图)。
	 */
	@Override
	public void hideModeCross() {

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

	}

	/**
	 * 关闭道路信息回调。
	 */
	@Override
	public void hideLaneInfo() {

	}

	/**
	 * 算路成功回调
	 * @param ints
	 */
	@Override
	public void onCalculateRouteSuccess(int[] ints) {

	}

	/**
	 * 通知当前是否显示平行路切换
	 * @param i
	 */
	@Override
	public void notifyParallelRoad(int i) {

	}

	/**
	 * 巡航模式（无路线规划）下，道路设施信息更新回调
	 * @param aMapNaviTrafficFacilityInfos
	 */
	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

	}

	/**
	 * 巡航模式（无路线规划）下，统计信息更新回调 连续5个点大于15km/h后开始回调
	 * @param aimLessModeStat
	 */
	@Override
	public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

	}

	/**
	 * 巡航模式（无路线规划）下，统计信息更新回调 当拥堵长度大于500米且拥堵时间大于5分钟时回调.
	 * @param aimLessModeCongestionInfo
	 */
	@Override
	public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

	}

	/**
	 * 回调各种类型的提示音，类似高德导航"叮".
	 * @param i
	 */
	@Override
	public void onPlayRing(int i) {

	}

	/**
	 * 界面右下角功能设置按钮的回调接口。
	 */
	@Override
	public void onNaviSetting() {

	}

	/**
	 * 导航页面左下角返回按钮点击后弹出的『退出导航对话框』中选择『确定』后的回调接口
	 */
	@Override
	public void onNaviCancel() {
		mTtsManager.stopSpeaking();
		finish();
	}

	/**
	 * 导航页面左下角返回按钮的回调接口 false-由SDK主动弹出『退出导航』对话框，true-SDK不主动弹出『退出导航对话框』，由用户自定义
	 * @return
	 */
	@Override
	public boolean onNaviBackClick() {
		return false;
	}

	/**
	 * 导航界面地图状态的回调。
	 * @param i
	 */
	@Override
	public void onNaviMapMode(int i) {

	}

	/**
	 * 界面左上角转向操作的点击回调。
	 */
	@Override
	public void onNaviTurnClick() {

	}

	/**
	 * 界面下一道路名称的点击回调。
	 */
	@Override
	public void onNextRoadClick() {

	}

	/**
	 * 界面全览按钮的点击回调。
	 */
	@Override
	public void onScanViewButtonClick() {

	}

	/**
	 * 是否锁定地图的回调
	 * @param b
	 */
	@Override
	public void onLockMap(boolean b) {

	}

	/**
	 * 导航view加载完成回调。
	 */
	@Override
	public void onNaviViewLoaded() {

	}
}
