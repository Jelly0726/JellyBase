package com.jelly.jellybase.weixinpay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by BYPC006 on 2016/8/11.
 */
public class WeixinPayInfo implements Serializable {
	private String appid;
	private String noncestr;
	@SerializedName("package")
	private String packageX;
	private String partnerid;
	private String prepayid;
	private String sign;
	private String timestamp;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getPackageX() {
		return packageX;
	}

	public void setPackageX(String packageX) {
		this.packageX = packageX;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
