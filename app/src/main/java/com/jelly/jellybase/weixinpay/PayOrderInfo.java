package com.jelly.jellybase.weixinpay;

/**
 * Created by BYPC006 on 2016/8/11.
 */
public class PayOrderInfo {
	private String orderno;
	private String driverId;
	private float amount;
	private String subject;
	private String paytype = "3";                   // 自定义支付类型,3:微信支付

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
}
