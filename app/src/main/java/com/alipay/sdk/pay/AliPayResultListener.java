package com.alipay.sdk.pay;
/**
 * 征游支付结果监听
 * @author Administrator
 *
 */
public interface AliPayResultListener {
	/**
	 *  成功
	 */
	public void onSucceed(String result);
	/**
	 * 失败
	 */
	public void onFailure(int code, String result);
}
