package com.base.http.DataBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BYPC006 on 2017/5/12.
 */

public class BankInfoGson implements Serializable {

	/**
	 * status : 1
	 * msg : 成功
	 * data : [{"bankname":"工商银行"},{"bankname":"建设银行"},{"bankname":"兴业银行"}]
	 */

	private int status;
	private String msg;
	/**
	 * bankname : 工商银行
	 */

	private List<DataBean> data;

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

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean implements Serializable {
		private String bankname;

		public String getBankname() {
			return bankname;
		}

		public void setBankname(String bankname) {
			this.bankname = bankname;
		}
	}
}
