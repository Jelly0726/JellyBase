package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.PathPlanningErrCode;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.base.MapUtil.SimpleNaviActivity;
import com.base.MapUtil.TTSController;
import com.base.appManager.BaseApplication;
import com.base.toast.ToastUtils;
import com.base.eventBus.HermesManager;
import com.base.eventBus.NetEvent;
import com.base.mprogressdialog.MProgressUtil;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.server.LocationService;
import com.maning.mndialoglibrary.MProgressDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by Administrator on 2018/3/13.
 */

public class AMapActivity extends BaseActivity implements AMapNaviListener ,AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener,LocationSource,AMap.OnMarkerClickListener{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.starNavi_layout)
    LinearLayout starNavi_layout;
    @BindView(R.id.aMapView)
    MapView aMapView;                                    //高德地图
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private OnLocationChangedListener mListener;
    //导航View
    private AMapNavi mAMapNavi;
    private TTSController mTtsManager;
    // 规划线路
    private RouteOverLay mRouteOverLay;
    private ArrayList<RouteOverLay> routeOverLays = new ArrayList<RouteOverLay>();
    private boolean mIsCalculateRouteSuccess = false;           // 是否计算成功的标志
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    private MProgressDialog progressDialog;
    private double latitude =0d;
    private double longitude=0d;
    private String address="";
    private String name="";
    private static float zoom=14f;                                      //地图缩放
    private boolean isFirstTime=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latitude=getIntent().getDoubleExtra("latitude",0d);
        longitude=getIntent().getDoubleExtra("longitude",0d);
        address=getIntent().getStringExtra("address");
        name=getIntent().getStringExtra("name");

        setContentView(R.layout.amap_activity);
        ButterKnife.bind(this);
        progressDialog = MProgressUtil.getInstance().getMProgressDialog(this);
        initAmap(savedInstanceState);
        initNavi();

        HermesEventBus.getDefault().register(this);
        HermesManager.getHermesManager().addEvent(this);
        Intent intent=new Intent(this, LocationService.class);
        startService(intent);
    }
    private void initAmap(Bundle savedInstanceState){
        aMapView.onCreate(savedInstanceState);//必须写
        if (aMap == null) {
            aMap = aMapView.getMap();
            // 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
            // 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
            // 如果要设置定位的默认状态，可以在此处进行设置
            myLocationStyle = new MyLocationStyle();
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//            //以下三种模式从5.1.0版本开始提供
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
//            myLocationStyle.anchor(0.5f, 1f);//设置定位蓝点图标的锚点方法。
//            myLocationStyle.strokeColor(Color.BLUE);//设置定位蓝点精度圆圈的边框颜色的方法。
//            myLocationStyle.radiusFillColor(Color.BLUE);//设置定位蓝点精度圆圈的填充颜色的方法。
//            myLocationStyle.strokeWidth(0.2f);//设置定位蓝点精度圈的边框宽度的方法。
//            myLocationStyle.interval(1000);//设置定位频次方法，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将按照1000计算。该方法只会作用在会执行连续定位的工作模式上。
//            myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

            aMap.setOnMapLoadedListener(this);
            aMap.setLocationSource(this);
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMarkerClickListener(this);//设置标记点击监听
            //设置SDK 自带定位消息监听
//            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    Log.i("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
//                }
//            });
//            aMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));//设置缩放级别

            aMap.getUiSettings().setZoomControlsEnabled(true);//缩放按钮控件是否显示
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setCompassEnabled(true);//指南针控件是否显示
            aMap.getUiSettings().setScaleControlsEnabled(true);//控制比例尺控件是否显示
            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置logo位置
        }
    }
    private void initNavi(){
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
    }
    @OnClick({R.id.left_back,R.id.starNavi_layout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.starNavi_layout:
                if (mStartPoints.size()<=0){
                    ToastUtils.showToast(this,"定位中...");
                    return;
                }
                if (latitude==0d||longitude==0d){
                    ToastUtils.showToast(this,"门店经纬度异常！");
                    return;
                }
                calculateDriveRoute();
                break;
        }
    }
    @Override
    protected void onResume() {
        aMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        aMapView.onPause();
        mTtsManager.stopSpeaking();
        super.onPause();
//        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
//        mAMapNavi.stopNavi();
    }

    @Override
    protected void onDestroy() {
        mAMapNavi.stopNavi();
        if(aMapView!=null) {
            aMapView.onDestroy();
        }
        if(aMap!=null) {
            aMap.clear();
            aMap = null;
        }
        aMapView=null;
        mListener = null;
        HermesEventBus.getDefault().unregister(this);
        HermesManager.getHermesManager().removeEvent(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        aMapView.onSaveInstanceState(outState);

    }
    /**
     * 计算驾车路线
     */

    private void calculateDriveRoute() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        mEndPoints.clear();
        NaviLatLng  mNaviEnd = new NaviLatLng(latitude,longitude);
        mEndPoints.add(mNaviEnd);
        try {
            //congestion 躲避拥堵,avoidhightspeed 不走高速,cost 避免收费,hightspeed 高速优先,multipleroute 多路径
            int strategy=mAMapNavi.strategyConvert(true, false, false, false, false);
            boolean isSuccess = mAMapNavi.calculateDriveRoute(mStartPoints,
                    mEndPoints, null, strategy);
            if (!isSuccess) {
                ToastUtils.show(this,"路线计算失败,检查参数情况");
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }catch (Exception e){
            ToastUtils.show(this,e.toString());
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }
    /**
     * 视图范围改变时回调
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }
    /**
     * 视图范围改变结束回调
     * @param cameraPosition
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
    }
    /**
     * 当地图载入成功后回调此方法。
     */
    @Override
    public void onMapLoaded() {
        if (latitude!=0d||longitude!=0d){
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(latitude, longitude));
            markerOption.title(name).snippet(address);

            markerOption.draggable(false);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.defaultMarker());
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(false);//设置marker平贴地图效果
            aMap.addMarker(markerOption);
        }
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
     * 路径规划失败后的回调函数
     *
     */
    @Override
    public void onCalculateRouteFailure(int arg0) {
        mIsCalculateRouteSuccess = false;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        String msg = "路径计算失败";
        switch (arg0){
//                case PathPlanningErrCode.SUCCESS_ROUTE:
//                    msg = arg0+"-路径计算成功";
//                    break;
            case PathPlanningErrCode.ERROR_CONNECTION:
                msg = arg0+"-路径计算错误异常：网络超时或网络失败";
                break;
            case PathPlanningErrCode.ERROR_STARTPOINT:
                msg = arg0+"-路径计算错误异常：起点错误";
                break;
            case PathPlanningErrCode.ERROR_PROTOCOL:
                msg = arg0+"-路径计算错误异常：协议解析错误";
                break;
            case PathPlanningErrCode.ERROR_ENDPOINT:
                msg = arg0+"-路径计算错误异常：终点错误";
                break;
            case PathPlanningErrCode.ERROR_NOROADFORSTARTPOINT:
                msg = arg0+"-路径计算错误异常：起点没有找到道路";
                break;
            case PathPlanningErrCode.ERROR_NOROADFORENDPOINT:
                msg = arg0+"-路径计算错误异常：终点没有找到道路";
                break;
            case PathPlanningErrCode.ERROR_NOROADFORWAYPOINT:
                msg = arg0+"-路径计算错误异常：途径点没有找到道路";
                break;
            case PathPlanningErrCode.INVALID_USER_KEY:
                msg = arg0+"-用户key非法或过期";
                break;
            case PathPlanningErrCode.SERVICE_NOT_EXIST:
                msg = arg0+"-请求服务不存在";
                break;
            case PathPlanningErrCode.SERVICE_RESPONSE_ERROR:
                msg = arg0+"-请求服务响应错误";
                break;
            case PathPlanningErrCode.INSUFFICIENT_PRIVILEGES:
                msg = arg0+"-无权限访问此服务";
                break;
            case PathPlanningErrCode.OVER_QUOTA:
                msg = arg0+"-请求超出配额";
                break;
            case PathPlanningErrCode.INVALID_PARAMS:
                msg = arg0+"-请求参数非法";
                break;
            case PathPlanningErrCode.UNKNOWN_ERROR:
                msg = arg0+"-未知错误";
                break;
            default:
                msg = arg0+"-路径计算失败";
                break;
        }
        Toast.makeText(AMapActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

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
     * 路径规划成功后的回调函数
     *
     */
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        mIsCalculateRouteSuccess = true;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        AMapNaviPath naviPath = mAMapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }
        Intent intent = new Intent(AMapActivity.this,
                SimpleNaviActivity.class);
        intent.putExtra("gps", true);//实时导航 true，模拟导航 false
        startActivity(intent);
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
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetEvent netEvent){
        Log.i("SSSS","netEvent.getEventType()="+netEvent.getEventType());
        if (netEvent.getEventType().equals(AMapLocation.class.getName())){
//            Intent intent=new Intent(BaseApplication.getInstance(), LocationService.class);
//            BaseApplication.getInstance().stopService(intent);
            if(isFirstTime) {
                AMapLocation aMapLocation = (AMapLocation) netEvent.getEvent();
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                isFirstTime=false;
                mStartPoints.clear();
                NaviLatLng  mStart = new NaviLatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                mStartPoints.add(mStart);
                if (aMapLocation.getLatitude()!=0d&&aMapLocation.getLongitude()!=0d){
                    Intent stateGuardService =  new Intent(BaseApplication.getInstance(), LocationService.class);
                    BaseApplication.getInstance().stopService(stateGuardService);
                }
            }
        }
    }
}
