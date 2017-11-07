package com.base.network.RetrofitApi;


import java.io.Serializable;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResult<T> implements Serializable {
	private int status;
	private String msg;
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
