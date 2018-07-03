package com.jelly.jellybase.weixinpay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.base.appManager.MyApplication;
import com.base.config.BaseConfig;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by BYPC006 on 2016/8/11.
 */
public class PayUtil {
	public static final int SDK_PAY_FLAG = 1;
	public static final int SDK_AUTH_FLAG = 2;
	//微信支付
	public static final String WX_RECHARGE_SUCCESS = MyApplication.getMyApp().getPackageName()+".WX_RECHARGE_SUCCESS";

	private static volatile PayUtil mInstance;
	private IWXAPI mApi;
	private Activity mContext;
	private Handler mHandler;


	private PayUtil(){
	}

	// 单例模式获取唯一对象
	static public PayUtil getInstance(final Activity context){
		if (mInstance == null){
			synchronized (context){
				if (mInstance == null){
					mInstance = new PayUtil();
					mInstance.mContext = context;
					// 通过WXAPIFactory工厂，获取IWXAPI的实例
					mInstance.mApi = WXAPIFactory.createWXAPI(context, BaseConfig.WechatPay_APP_ID);
					// 将该app注册到微信
					mInstance.mApi.registerApp(BaseConfig.WechatPay_APP_ID);
					// 初始化UI Handler
					mInstance.mHandler = new Handler(Looper.getMainLooper());
				}else {
					mInstance.mContext = context;
				}
			}
		}
		return mInstance;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Activity context) {
		mContext = context;
	}

	/***
	 * 微信支付接口
	 * @param payStruct
	 */
	public void wxinPay(WeixinPayInfo payStruct){
		PayReq request = new PayReq();

		request.appId = payStruct.getAppid();
		request.partnerId = payStruct.getPartnerid();
		request.prepayId= payStruct.getPrepayid();
		request.packageValue = payStruct.getPackageX();
		request.nonceStr= payStruct.getNoncestr();
		request.timeStamp= payStruct.getTimestamp();
		request.sign= payStruct.getSign();
		int wxSdkVersion = mApi.getWXAppSupportAPI();
		if (wxSdkVersion >= Build.PAY_SUPPORTED_SDK_INT){
			mApi.sendReq(request);
		}else {
			Toast.makeText(mContext, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
		}
	}
}
