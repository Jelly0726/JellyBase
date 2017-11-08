package com.base.httpmvp.retrofitapi;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResult<T> {
	@SerializedName("status")
	private int status;
	@SerializedName(value ="msg", alternate = "message")
	private String msg;
	@SerializedName("data")
	private T data;

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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
