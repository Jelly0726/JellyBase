package com.alipay.sdk.pay;

import java.io.Serializable;

/**
 * 订单
 * @author Administrator
 *
 */
public class OrderInfo implements Serializable {
    private String RSA_PRIVATE;// 商户（RSA）私钥(注意一定要转PKCS8格式，否则在Android4.0及以上系统会支付失败)
    private String RSA_PUBLIC;//支付宝公钥
	private String subject;//商品名
	private String body ;//  商品说明
	private String price ;// 商品价格
	private String merchantId ;//游戏商ID
	private String merchantAppId;// 游戏应用ID
	private String appId ;//应用程序ID
	private String playcode;//用户账号
	private String exOrderNo;//游戏服务器订单号
	private String ZhengyouOrderNo;//征游订单号  唯一
	private String extraParam;// 商户私钥信息
	private String partner;//商家ID
	private String seller_id;//商家收款账号
	private String notify_url;//服务器异步通知页面路径
	private String sign;//签名
	private String payTime;//交易时间
	private String intoSign;//支付宝格式的订单信息
	public String getIntoSign() {
		return intoSign;
	}
	public void setIntoSign(String intoSign) {
		this.intoSign = intoSign;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantAppId() {
		return merchantAppId;
	}
	public void setMerchantAppId(String merchantAppId) {
		this.merchantAppId = merchantAppId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getPlayCode() {
		return playcode;
	}
	public void setPlayCode(String playcode) {
		this.playcode = playcode;
	}
	public String getExOrderNo() {
		return exOrderNo;
	}
	public void setExOrderNo(String exOrderNo) {
		this.exOrderNo = exOrderNo;
	}
	public String getExtraParam() {
		return extraParam;
	}
	public void setExtraParam(String extraParam) {
		this.extraParam = extraParam;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getRSA_PRIVATE() {
		return RSA_PRIVATE;
	}
	public void setRSA_PRIVATE(String rSA_PRIVATE) {
		RSA_PRIVATE = rSA_PRIVATE;
	}
	public String getRSA_PUBLIC() {
		return RSA_PUBLIC;
	}
	public void setRSA_PUBLIC(String rSA_PUBLIC) {
		RSA_PUBLIC = rSA_PUBLIC;
	}
	public String getZhengyouOrderNo() {
		return ZhengyouOrderNo;
	}
	public void setZhengyouOrderNo(String zhengyouOrderNo) {
		ZhengyouOrderNo = zhengyouOrderNo;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
}
