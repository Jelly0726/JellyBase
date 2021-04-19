package com.base.MapUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapCongestionLink;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.base.MapUtil.mscUtil.TTSController;
import com.base.BaseApplication;

import java.io.ObjectStreamException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2016/8/25.
 */
public class AMapNaviTask implements AMapNaviListener {
    private AMapNavi mAMapNavi;
    private AMap aMap;
    private Marker myLocationMarker;
    private Activity activity;
    private TTSController mTtsManager;
    private boolean isStar=false;//规划完成是否开始导航
    private boolean isculateRoute=false;//是否规划完成
    private ProgressDialog mRouteCalculatorProgressDialog;      // 路径规划过程显示状态
    // 是否需要跟随定位
    private boolean isNeedFollow = true;
    // 处理静止后跟随的timer
    private Timer needFollowTimer;
    //屏幕静止DELAY_TIME之后，再次跟随
    private long DELAY_TIME = 5000;
    private AMapNaviTask(){
        mTtsManager = TTSController.getInstance(BaseApplication.getInstance());
        mAMapNavi = AMapNavi.getInstance(BaseApplication.getInstance());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
    }
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final AMapNaviTask instance = new AMapNaviTask();
    }
    public static synchronized AMapNaviTask getInstance(){
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
     * 开始导航
     * @param isEMULATOR  模拟导航 true  GPS导航false
     */
    public void startNavi(boolean isEMULATOR){
        if (isculateRoute) {
            if (!isEMULATOR)
                mAMapNavi.startNavi(NaviType.GPS);
            else {
                mAMapNavi.startNavi(NaviType.EMULATOR);
            }
        } else {
            if (activity != null) {
                Toast.makeText(activity, "请先进行路径规划！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 开启巡航（无路线规划）要连接互联网
     * @param AimLessMode 设置在巡航模式（无路线规划）的状态下，智能播报的类型
     */
    public void startAimlessMode(int AimLessMode){
        //开启巡航 设置在巡航模式（无路线规划）的状态下，智能播报的类型
        mAMapNavi.startAimlessMode(AimLessMode);
        setMapInteractiveListener();
    }

    /**
     * 关闭巡航（无路线规划）
     */
    public void stopAimlessMode(){
        //关闭巡航。设置在非导航巡航的状态下，智能播报的类型
        mAMapNavi.stopAimlessMode();
    }
    /**
     * 路径规划
     * @param mStartPoints  起点  当为null时为无起点算路 起点默认为当前位置
     * @param mEndPoints    终点
     * @param wayPoints    途径点
     * @param strategy     算路策略
     * @param isStar     是否规划成功后自动开始导航
     */
    public void calculateRoute(List<NaviLatLng> mStartPoints
            , List<NaviLatLng> mEndPoints
            ,List<NaviLatLng> wayPoints
            ,int strategy,boolean isStar){
        this.isStar=isStar;
        if(mEndPoints!=null) {
            if (mEndPoints.size() > 0) {
                if (mStartPoints != null) {
                    if (mStartPoints.size() > 0) {
                        mAMapNavi.calculateDriveRoute(mStartPoints,
                                mEndPoints, wayPoints, strategy);
                    } else {
                        mAMapNavi.calculateDriveRoute(mEndPoints, wayPoints, strategy);
                    }
                } else {
                    mAMapNavi.calculateDriveRoute(
                            mEndPoints, wayPoints, strategy);
                }
                if(activity!=null) {
                    if (mRouteCalculatorProgressDialog == null) {
                        mRouteCalculatorProgressDialog = new ProgressDialog(activity);
                        mRouteCalculatorProgressDialog.setCancelable(true);
                    }
                    if (!mRouteCalculatorProgressDialog.isShowing()) {
                        mRouteCalculatorProgressDialog.show();
                    }
                }
            }else {
                if(activity!=null){
                    Toast.makeText(activity,"请设置目的地！",Toast.LENGTH_LONG).show();
                }
            }
        }else {
            if(activity!=null){
                Toast.makeText(activity,"请设置目的地！",Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * 开始语音播报
     */
    public void startSpeaking(){
        if(mTtsManager!=null) {
//            mTtsManager.startSpeaking();
        }
    }
    /**
     * 停止语音播报
     */
    public void stopSpeaking(){
        if(mTtsManager!=null) {
            mTtsManager.stopSpeaking();
        }
    }

    /**
     * 若您想实时将您的巡航位置记录在地图上，看到您的巡航状态，您可创建 MapView 对象获取AMap，
     * 在地图上添加 Marker，并通过自车回调 onLocationChange
     * 回调获取的自车位置实时更新 Marker 的坐标，此处可参考官网Demo中的“智能巡航”。
     * @param aMap
     * @param myLocationMarker   地图上添加 Marker
     */
    public void setAMap(AMap aMap,Marker myLocationMarker){
        this.aMap=aMap;
        // 初始化 显示我的位置的Marker
        this.myLocationMarker = myLocationMarker;
    }
    /**
     * 设置导航监听
     */
    private void setMapInteractiveListener() {
        if(aMap!=null) {
            aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // 按下屏幕
                            // 如果timer在执行，关掉它
                            clearTimer();
                            // 改变跟随状态
                            isNeedFollow = false;
                            break;
                        case MotionEvent.ACTION_UP:
                            // 离开屏幕
                            startTimerSomeTimeLater();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * 取消timer任务
     */
    private void clearTimer() {
        if (needFollowTimer != null) {
            needFollowTimer.cancel();
            needFollowTimer = null;
        }
    }
    /**
     * 如果地图在静止的情况下
     */
    private void startTimerSomeTimeLater() {
        // 首先关闭上一个timer
        clearTimer();
        needFollowTimer = new Timer();
        // 开启一个延时任务，改变跟随状态
        needFollowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isNeedFollow = true;
            }
        }, DELAY_TIME);
    }
    /**
     * 销毁导航
     */
    public void onDestroy() {
        if(mAMapNavi!=null) {
            mAMapNavi.stopNavi();
            mAMapNavi.stopAimlessMode();
            mAMapNavi.destroy();
            mAMapNavi=null;
        }
        if(mTtsManager!=null) {
            mTtsManager.destroy();
            mTtsManager=null;
        }
    }

    /**
     * 暂停导航
     */
    public void onPause() {
        if(mAMapNavi!=null) {
            mAMapNavi.pauseNavi();
        }
    }

    /**
     * 设置上下文 activity用于显示show信息
     * @param activity
     */
    public void setActivity(Activity activity){
        this.activity=activity;
    }
    /**
     * 继续导航
     */
    public void onResume() {
        if(mAMapNavi!=null) {
            mAMapNavi.resumeNavi();
        }
    }
    /**
     * 移除导航监听
     * @return
     */
    public void removeAMapNaviListener(AMapNaviListener aMapNaviListener) {
        if(mAMapNavi!=null) {
            if (aMapNaviListener != null) {
                mAMapNavi.removeAMapNaviListener(aMapNaviListener);
            }
        }
    }
    /**
     * 设置导航监听
     * @return
     */
    public void addAMapNaviListener(AMapNaviListener aMapNaviListener) {
        if(mAMapNavi!=null) {
            if (aMapNaviListener != null) {
                mAMapNavi.addAMapNaviListener(aMapNaviListener);
            }
        }
    }
    /**
     * 导航创建失败时的回调函数。
     */
    @Override
    public void onInitNaviFailure() {
    }

    /**
     * 导航创建成功时的回调函数。
     */
    @Override
    public void onInitNaviSuccess() {
    }

    /**
     * 启动导航后回调函数
     * @param i
     */
    @Override
    public void onStartNavi(int i) {
    }

    /**
     * 当前方路况光柱信息有更新时回调函数。
     */
    @Override
    public void onTrafficStatusUpdate() {
    }

    /**
     * 当GPS位置有更新时的回调函数。
     * @param aMapNaviLocation
     */
    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        if (aMapNaviLocation != null) {
            LatLng latLng = new LatLng(aMapNaviLocation.getCoord().getLatitude(),
                    aMapNaviLocation.getCoord().getLongitude());
            // 显示定位小图标，初始化时已经创建过了，这里修改位置即可
            if(myLocationMarker!=null) {
                myLocationMarker.setPosition(latLng);
            }
            if (isNeedFollow) {
                // 跟随
                if(aMap!=null) {
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
                }
            }
        }
    }

    /**
     * 导航播报信息回调函数。
     * @param i
     * @param s
     */
    @Override
    public void onGetNavigationText(int i, String s) {
    }

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
     * 步行或者驾车路径规划成功后的回调函数。
     */
    @Override
    public void onCalculateRouteSuccess(int[] id) {
        isculateRoute=true;
        if(mRouteCalculatorProgressDialog!=null) {
            mRouteCalculatorProgressDialog.dismiss();
        }
        if (isStar) {
            startNavi(false);
        }
    }

    /**
     * 步行或者驾车路径规划失败后的回调函数。
     * @param i
     */
    @Override
    public void onCalculateRouteFailure(int i) {
        isculateRoute=false;
        if(mRouteCalculatorProgressDialog!=null) {
            mRouteCalculatorProgressDialog.dismiss();
        }
    }

    /**
     * 步行或驾车导航时,出现偏航后需要重新计算路径的回调函数。
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

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    /**
     * 已过时。
     1.8.0开始，不再回调该方法
     * @param aMapNaviTrafficFacilityInfo
     */
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
    }

    /**
     * 设置在非导航巡航的状态下，智能播报的类型 用户一旦设置，在非导航巡航的状态下，
     * 可以用于获得道路设施（包括电子眼，转弯提示等
     * @param aMapNaviTrafficFacilityInfos
     */
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }
    /**
     * 显示路口放大图回调。
     * @param aMapNaviCross
     */
    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
    }

    /**
     * 关闭路口放大图回调。
     */
    @Override
    public void hideCross() {
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    /**
     * 显示道路信息回调。
     * @param aMapLaneInfos
     * @param bytes
     * @param bytes1
     */
    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    /**
     * 关闭道路信息回调。
     */
    @Override
    public void hideLaneInfo() {
    }
    /**
     * 通知当前是否显示平行路切换
     * @param i
     */
    @Override
    public void notifyParallelRoad(int i) {
    }
    /**在巡航过程中，出现电子眼或者特殊道路设施时，会进到 updateAimlessModeCongestionInfo 回调中，
     * 通过 AimLessModeCongestionInfo 对象，可获取到道路交通设施信息（如：类型、距离设施的剩余距离等）。
     * 巡航模式（无路线规划）下，统计信息更新回调 当拥堵长度大于500米且拥堵时间大于5分钟时回调
     参数:
     aimLessModeStat - 巡航模式（无路线规划）下统计信息
     * @param aimLessModeCongestionInfo
     */
    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        Log.d("msg", "roadName=" + aimLessModeCongestionInfo.getRoadName());
        Log.d("msg", "CongestionStatus=" + aimLessModeCongestionInfo.getCongestionStatus());
        Log.d("msg", "eventLonLat=" + aimLessModeCongestionInfo.getEventLon() + "," + aimLessModeCongestionInfo.getEventLat());
        Log.d("msg", "length=" + aimLessModeCongestionInfo.getLength());
        Log.d("msg", "time=" + aimLessModeCongestionInfo.getTime());
        for (AMapCongestionLink link :
                aimLessModeCongestionInfo.getAmapCongestionLinks()) {
            Log.d("msg", "status=" + link.getCongestionStatus());
            for (NaviLatLng latlng : link.getCoords()
                    ) {
                Log.d("msg", latlng.toString());
            }
        }
    }
    /**
     * 巡航时，位置发生变化时，会触发 updateAimlessModeStatistics
     * 回调，告知您巡航的连续行驶距离和连续启用时间。
     * 巡航模式（无路线规划）下，统计信息更新回调 连续5个点大于15km/h后开始回调
     参数:
     * @param aimLessModeStat
     */
    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        Log.d("msg", "连续行驶距离distance=" + aimLessModeStat.getAimlessModeDistance());
        Log.d("msg", "连续启用时间time=" + aimLessModeStat.getAimlessModeTime());
    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onGpsSignalWeak(boolean b) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }
}
