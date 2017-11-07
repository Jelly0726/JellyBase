package com.base.http.Mode;

import com.base.http.DataBean.NetIPJson;


/**
 * Created by BYPC006 on 2017/6/23.
 */

public interface NetIPSourceInter {

	Long getUserId();

	void getData(final GetDataCallBack callBack);

	interface GetDataCallBack {
		void onError(String message);
		void onNext(NetIPJson.DataBean dataBeen);
	}
}
