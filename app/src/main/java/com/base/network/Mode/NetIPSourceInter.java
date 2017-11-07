package com.base.network.Mode;

import com.base.network.DataBean.NetIPJson;


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
