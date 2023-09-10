package com.base.httpmvp.retrofitapi.methods;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResult {
	@SerializedName(value ="status", alternate = "state")
	private int status;
	@SerializedName(value ="msg", alternate = "message")
	private String msg;

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
}
