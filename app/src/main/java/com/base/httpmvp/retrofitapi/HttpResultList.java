package com.base.httpmvp.retrofitapi;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResultList<T> {
	@SerializedName("status")
	private int status;
	@SerializedName(value ="msg", alternate = "message")
	private String msg;
	@SerializedName("data")
	private List<T> data=new ArrayList<>();

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

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}
