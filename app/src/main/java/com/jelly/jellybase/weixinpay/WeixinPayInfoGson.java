package com.jelly.jellybase.weixinpay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by BYPC006 on 2016/8/11.
 */
public class WeixinPayInfoGson implements Serializable {

	/**
	 * status : 1
	 * msg : 成功
	 * data : [{"appid":"wxdfb47afbe6cbdb43","noncestr":"vMwYssPVWKzSu0Za","package":"Sign=WXPay","partnerid":"1380342702","prepayid":"wx201608241750328e7493b7b50090027169","sign":"D7581C32CD79FC829AEE746E37E804A9","timestamp":"1472032182"}]
	 */

	private int status;
	private String msg;
	/**
	 * appid : wxdfb47afbe6cbdb43
	 * noncestr : vMwYssPVWKzSu0Za
	 * package : Sign=WXPay
	 * partnerid : 1380342702
	 * prepayid : wx201608241750328e7493b7b50090027169
	 * sign : D7581C32CD79FC829AEE746E37E804A9
	 * timestamp : 1472032182
	 */

	private ArrayList<DataBean> data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ArrayList<DataBean> getData() {
		return data;
	}

	public void setData(ArrayList<DataBean> data) {
		this.data = data;
	}

	public static class DataBean implements Serializable {
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
}
