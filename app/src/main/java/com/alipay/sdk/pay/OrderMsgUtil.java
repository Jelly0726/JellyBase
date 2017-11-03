package com.alipay.sdk.pay;

import java.io.Serializable;

/**
 * 对外提供参数对象
 * @author Administrator
 *
 */
public class OrderMsgUtil implements Serializable {
	private String exOrderNo;//游戏服务器的唯一单号
	private String agentSign;// 交易签名(由游戏商服务器生成的唯一签名，用于服务器异步通知时的验签)
	private String subject;//商品名称
	private String mhtOrderType="01";//消费类型  01 普通消费
	private String mhtCurrencyType="156";//交易币种  156 人民币
	private String price;//交易金额
	private String mhtOrderDetail;//订单说明
	private String mhtOrderTimeOut="3600";//超时时间
	private String mhtCharset="UTF-8";//转码
	private String playcode;//玩家账号
	private String playname;//玩家名称
	private int payType=0;//支付方式 0征游余额支付 1 支付宝 2 微信 3 QQ支付 4 银联支付
	private int type=0;//0 消费  1充值

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPlaycode() {
		return playcode;
	}
	public void setPlaycode(String playcode) {
		this.playcode = playcode;
	}
	public String getPlayname() {
		return playname;
	}
	public void setPlayname(String playname) {
		this.playname = playname;
	}
	public String getExOrderNo() {
		return exOrderNo;
	}
	public void setExOrderNo(String exOrderNo) {
		this.exOrderNo = exOrderNo;
	}
	public String getAgentSign() {
		return agentSign;
	}
	public void setAgentSign(String agentSign) {
		this.agentSign = agentSign;
	}
	public String getMhtOrderType() {
		return mhtOrderType;
	}
	public void setMhtOrderType(String mhtOrderType) {
		this.mhtOrderType = mhtOrderType;
	}
	public String getMhtCurrencyType() {
		return mhtCurrencyType;
	}
	public void setMhtCurrencyType(String mhtCurrencyType) {
		this.mhtCurrencyType = mhtCurrencyType;
	}
	public String getMhtOrderDetail() {
		if(mhtOrderDetail==null){
			if(subject!=null){
				mhtOrderDetail=subject;
			}else{
				return "游戏币";
			}
		}
		
		return mhtOrderDetail;
	}
	public void setMhtOrderDetail(String mhtOrderDetail) {
		this.mhtOrderDetail = mhtOrderDetail;
	}
	public String getMhtOrderTimeOut() {
		return mhtOrderTimeOut;
	}
	public void setMhtOrderTimeOut(String mhtOrderTimeOut) {
		this.mhtOrderTimeOut = mhtOrderTimeOut;
	}
	public String getMhtCharset() {
		return mhtCharset;
	}
	public void setMhtCharset(String mhtCharset) {
		this.mhtCharset = mhtCharset;
	}
	public int getPayType() {
		return payType;
	}
	public void setPayType(int payType) {
		this.payType = payType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
