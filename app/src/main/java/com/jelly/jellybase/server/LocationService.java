package com.jelly.jellybase.server;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.base.MapUtil.LocationTask;
import com.base.MapUtil.OnLocationGetListener;
import com.base.MapUtil.RegeocodeTask;
import com.base.MapUtil.RouteTask;
import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.config.ConfigKey;
import com.base.eventBus.LocationTypeEvent;
import com.base.eventBus.NetEvent;
import com.jelly.jellybase.R;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;

import systemdb.PositionEntity;
import xiaofei.library.hermeseventbus.HermesEventBus;


/**gps定位服务
 * Created by Administrator on 2016/9/9.
 */
public class LocationService extends Service {
    public static final String TAG = LocationService.class.getName();
    private LocationTask mLocationTask;                                                             // 自定义定位类
    private RegeocodeTask regeocodeTask;                                                            // 自定义坐标转换类
    //private Login login;
    private PositionEntity entity = new PositionEntity();
    private LocationTypeEvent mLocationTypeEvent = new LocationTypeEvent();
    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.000000");
    private DecimalFormat mDf = new DecimalFormat("#####0.0");
    private LatLng mLatLngPre = new LatLng(0, 0);                                                   // 前一个坐标数据
    private LatLng mLatLngNow = new LatLng(0, 0);                                                   // 当前获取坐标数据
    private String address;
    private String city;
    private String adCode;
    private String district;//城区

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //==================================================================//后台定位
        regeocodeTask=new RegeocodeTask(MyApplication.getMyApp());
        regeocodeTask.setOnLocationGetListener(onLocationGetListener);
        mLocationTask = LocationTask.getInstance(MyApplication.getMyApp());
        mLocationTask.setOnLocationGetListener(onLocationGetListener);
        if (!HermesEventBus.getDefault().isRegistered(LocationService.this)){
            HermesEventBus.getDefault().register(LocationService.this);
        }
        //==================================================================//
        AppPrefs.putBoolean(MyApplication.getMyApp(), ConfigKey.ISRUN,true);
        // 开启高精度定位
        mLocationTypeEvent.setType(LocationTypeEvent.sTYPE_HIGHT_PRECISION);
        HermesEventBus.getDefault().post(mLocationTypeEvent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mLocationTask.getLocationStatus()){
            // 开启定位
            HermesEventBus.getDefault().post(mLocationTypeEvent);
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLocationTask != null) {
            mLocationTask.RemoveListener(onLocationGetListener);
            mLocationTask.stopLocate();
        }
        HermesEventBus.getDefault().unregister(LocationService.this);
        AppPrefs.putBoolean(MyApplication.getMyApp(), ConfigKey.ISRUN,false);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
    private OnLocationGetListener onLocationGetListener=new OnLocationGetListener(){
        /**
         * 定位回调
         * @param amapLocation
        1	GPS定位结果	通过设备GPS定位模块返回的定位结果
        2	前次定位结果	网络定位请求低于1秒、或两次定位之间设备位置变化非常小时返回，设备位移通过传感器感知。
        4	缓存定位结果	返回一段时间前设备在同样的位置缓存下来的网络定位结果
        5	Wifi定位结果	属于网络定位，定位精度相对基站定位会更好
        6	基站定位结果	属于网络定位
        8	离线定位结果
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onLocationGet(AMapLocation amapLocation) {
            //定位次数判断是否定位成功的标记
            //util.setLocatConnt(util.getLocatConnt()+1);
            //定位模式修改;当卫星数大于3,则开启gps定位,否则开启高精度定位
            if (amapLocation.getErrorCode() != 0){
                locationError(amapLocation.getErrorCode());
                return;
            }
            if (!getLocationType(amapLocation)){
                return;
            }
//            if(MyApplication.orderNu.equals("0") ) {
//                if ( mLocationTypeEvent.getType() != LocationTypeEvent.sTYPE_LBS){
//                    // 开启lbs模式
//                    mLocationTypeEvent.setType(LocationTypeEvent.sTYPE_LBS);
//                    HermesEventBus.getDefault().post(mLocationTypeEvent);
//                }
//            }else {
//                final int  starNum = amapLocation.getSatellites();
//                if( starNum < 4) {
//                    // mag.append("GPS 卫星少于3颗" + "\n");
//                    if (mLocationTypeEvent.getType() != LocationTypeEvent.sTYPE_HIGHT_PRECISION){
//                        // mag.append("开启High精度模式" + "\n");
//                        mLocationTypeEvent.setType(LocationTypeEvent.sTYPE_HIGHT_PRECISION);
//                        HermesEventBus.getDefault().post(mLocationTypeEvent);
//                    }
//                }else {
//                    if (mLocationTypeEvent.getType() != LocationTypeEvent.sTYPE_GPS){
//                        // mag.append("开启gps模式" + "\n");
//                        mLocationTypeEvent.setType(LocationTypeEvent.sTYPE_GPS);
//                        HermesEventBus.getDefault().post(mLocationTypeEvent);
//                    }
//                }
//            }
////            Log.d(TAG, "定时器定位");
//            MyApplication.latitude = Double.parseDouble(mDecimalFormat.format(amapLocation.getLatitude()));
//            MyApplication.longitude = Double.parseDouble(mDecimalFormat.format(amapLocation.getLongitude()));
//            MyApplication.provider =amapLocation.getProvider();
//            entity.latitue = amapLocation.getLatitude();
//            entity.longitude = amapLocation.getLongitude();
////            PostDriverXY.postErrorMessage(MyApplication.mDriverId, "x:"+MyApplication.latitude+"y:"+MyApplication.longitude);
//            //Log.i("msg","getProvider()="+amapLocation.getProvider()+" getLocationType="+amapLocation.getLocationType());
            entity.latitue = amapLocation.getLatitude();
            entity.longitude = amapLocation.getLongitude();
            if (amapLocation.getProvider().equals("gps")) {
                mLatLngNow = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                float distance = AMapUtils.calculateLineDistance(mLatLngNow, mLatLngPre);
                if (distance > 50){            // 距离上一个坐标点大于 50m才进行地址解析
                    regeocodeTask.search(amapLocation.getLatitude(), amapLocation.getLongitude());
                    mLatLngPre = mLatLngNow;
                }
            } else {
                address = amapLocation.getAddress();
                city = amapLocation.getCity();
                adCode = amapLocation.getAdCode().substring(0, 6);
                district = amapLocation.getDistrict();
                entity.address = address;
                entity.province = amapLocation.getProvince();
                entity.city = city;
                entity.district = district;
                entity.adCode=adCode;
                RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
                if (MyApplication.getMyApp().getEventSize()>0) {
                    NetEvent netEvent1 = new NetEvent();
                    netEvent1.setEvent(entity);
                    HermesEventBus.getDefault().post(netEvent1);
                }
            }
            NetEvent netEvent = new NetEvent();
            netEvent.setEvent(amapLocation.clone());
            HermesEventBus.getDefault().post(netEvent);
        }

        /**
         * 坐标转地址回调
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onRegecodeGet(RegeocodeResult regeocodeReult) {
            if (regeocodeReult == null ) {

            }else{
               address=regeocodeReult.getRegeocodeAddress().getFormatAddress();
                if(regeocodeReult.getRegeocodeAddress().getFormatAddress().isEmpty()
                        ||regeocodeReult.getRegeocodeAddress().getFormatAddress().equals("null")){
                }
                if(regeocodeReult.getRegeocodeAddress().getFormatAddress().length()!=0
                        &&!regeocodeReult.getRegeocodeAddress().getFormatAddress().equals("0")){
                    entity.province = regeocodeReult.getRegeocodeAddress().getProvince();
                    city=regeocodeReult.getRegeocodeAddress().getCity();
                    district=regeocodeReult.getRegeocodeAddress().getDistrict();
                    adCode=regeocodeReult.getRegeocodeAddress()
                            .getAdCode().substring(0,6);
                    stopSelf();
                }else{
                }
            }
            entity.address = address;
            entity.city = city;
            entity.city = district;
            entity.adCode=adCode;
            RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
            if (MyApplication.getMyApp().getEventSize()>0) {
                NetEvent netEvent = new NetEvent();
                netEvent.setEvent(entity);
                HermesEventBus.getDefault().post(netEvent);
            }
        }
        /**
         * 地址转坐标回调
         */
        @Override
        public void onGecodeGet(GeocodeResult geocodeReult) {

        }
    };

	/***
     * 订阅 定位模式切换函数
     * @param locationTypeEvent
     */
    @Subscribe
    public void onEvent(LocationTypeEvent locationTypeEvent){
        switch (locationTypeEvent.getType()){
            case LocationTypeEvent.sTYPE_HIGHT_PRECISION:
                mLocationTask.startLocate(6000, 0);
                break;
            case LocationTypeEvent.sTYPE_LBS:
                mLocationTask.startLocate(6000,1);
                break;
            case LocationTypeEvent.sTYPE_GPS:
                mLocationTask.startLocate(6000,2);
                break;
            default:
                break;
        }
    }
	/***
     * 定位错误码返回信息
     * @param errorCode
     */
    public void locationError(int errorCode){
        String errorMessage = "";
        switch (errorCode){
            case 1:
                errorMessage = getResources().getString(R.string.location_error_code_1);
                break;
            case 2:
                errorMessage = getResources().getString(R.string.location_error_code_2);
                break;
            case 3:
                errorMessage = getResources().getString(R.string.location_error_code_3);
                break;
            case 4:
                errorMessage = getResources().getString(R.string.location_error_code_4);
                break;
            case 5:
                errorMessage = getResources().getString(R.string.location_error_code_5);
                break;
            case 6:
                errorMessage = getResources().getString(R.string.location_error_code_6);
                break;
            case 7:
                errorMessage = getResources().getString(R.string.location_error_code_7);
                break;
            case 8:
                errorMessage = getResources().getString(R.string.location_error_code_8);
                break;
            case 9:
                errorMessage = getResources().getString(R.string.location_error_code_9);
                break;
            case 10:
                errorMessage = getResources().getString(R.string.location_error_code_10);
                break;
            case 11:
                errorMessage = getResources().getString(R.string.location_error_code_11);
                break;
            case 12:
                errorMessage = getResources().getString(R.string.location_error_code_12);
                break;
            case 13:
                errorMessage = getResources().getString(R.string.location_error_code_13);
                break;
            case 14:
                errorMessage = getResources().getString(R.string.location_error_code_14);
                break;
            default:
                errorMessage = "定位失败, errorCode:" + errorCode;
                break;
        }
        entity.address = errorMessage;
        if (MyApplication.getMyApp().getEventSize()>0) {
            NetEvent netEvent = new NetEvent();
            netEvent.setEvent(entity);
            HermesEventBus.getDefault().post(netEvent);
        }
    }

	/***
     * 过滤定位结果
     * @param amapLocation
     * @return  定位结果保留返回true,定位结果过滤返回false
     */
    private boolean getLocationType(AMapLocation amapLocation){
        switch (amapLocation.getLocationType()){
            case 0:                 // 定位失败
                return false;
            case 1:                 // GPS定位结果    通过设备GPS定位模块返回的定位结果，精度较高，在10米－100米左右
                return true;
            case 2:                 // 前次定位结果   网络定位请求低于1秒、或两次定位之间设备位置变化非常小时返回，设备位移通过传感器感知。
                return true;
            case 4:                 // 缓存定位结果   返回一段时间前设备在同样的位置缓存下来的网络定位结果
                return true;
            case 5:                 // Wifi定位结果   属于网络定位，定位精度相对基站定位会更好，定位精度较高，在5米－200米之间。
                return true;
            case 6:
                return true;       // 基站定位结果   纯粹依赖移动、连通、电信等移动网络定位，定位精度在500米-5000米之间。
            case 8:
                return true;        // 离线定位结果
            default:
                break;
        }
        return true;
    }
}
