package com.base.httpmvp.retrofitapi;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResultData<T> extends HttpResult{
	@SerializedName("data")
	private T data;
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
