package com.base.httpmvp.retrofitapi;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpResultAll<T> {
	@SerializedName("returnState")
	private boolean returnState;
	@SerializedName(value ="msg", alternate = "message")
	private String msg;
	@SerializedName("data")
	private T data;
	/**
	 * API是否请求失败
	 *
	 * @return 成功返回true, 失败返回false
	 */
	public boolean isReturnState() {
		return returnState;
	}

	public void setReturnState(boolean status) {
		this.returnState = status;
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
