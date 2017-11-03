package com.jelly.jellybase.weixinpay;

import android.app.Activity;


/**
 * Created by BYPC006 on 2016/8/25.
 */
public class MyPayRequest {
	public static int sWeixinPayType = 0;                                                   // 微信支付调起类型, 用于区分广播发送对象
	public static final int sWEIXIN_PAY_TYPE_RECHARGE = 1;                              // 司机充值调起微信支付
	private static final int sHANDLER_DISPLAY = 0;                                       // 消息打印处理

	private static volatile MyPayRequest mInstance;
	private Activity mContext;


	private MyPayRequest(){
	}

	// 单例模式获取唯一对象
	static public MyPayRequest getInstance(final Activity context){
		if (mInstance == null){
			synchronized (context){
				if (mInstance == null){
					mInstance = new MyPayRequest();
					mInstance.mContext = context;
				}
			}
		}else {
			mInstance.mContext = context;
		}
		return mInstance;
	}

	/***
	 * 新订单要求后台服务器 统一下单
	 * @param payOrderInfo
	 */
	public void getWeiXinPayInfo(PayOrderInfo payOrderInfo){
		sWeixinPayType = sWEIXIN_PAY_TYPE_RECHARGE;
//		Subscriber<List<WeixinPayInfoGson.DataBean>> subscriber = new Subscriber<List<WeixinPayInfoGson.DataBean>>() {
//			@Override
//			public void onCompleted() {
//
//			}
//
//			@Override
//			public void onError(Throwable e) {
//				Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
//			}
//
//			@Override
//			public void onNext(List<WeixinPayInfoGson.DataBean> dataBeen) {
//				PayUtil.getInstance(mContext).wxinPay(dataBeen.get(0));
//			}
//		};
//		HttpMethods.getInstance().wxRecharge(subscriber, payOrderInfo.getDriverId(), String.valueOf(payOrderInfo.getAmount()));
	}
}
