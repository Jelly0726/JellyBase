package com.base.network.RetrofitApi;


import java.io.Serializable;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResultAll implements Serializable {
	private int code;
	private int status;
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

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
