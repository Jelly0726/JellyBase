package com.base.network.RetrofitApi;


import com.base.network.DataBean.BankInfoGson;
import com.base.network.DataBean.NetIPJson;
import com.base.network.DataBean.TokenModel;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public interface IApiService {
	@GET("get_token")
	Observable<TokenModel> getToken();
	@GET("refresh_token")
	Observable<TokenModel> refreshToken();
	//@GET("request")
	//Observable<ResultModel> getResult(@Query("token") String token);

	@GET("service/getIpInfo2.php?ip=myip")                                                                                                             // 获取司机充值明细信息
	Observable<NetIPJson.DataBean> getNetIP(@QueryMap Map<String, String> paramMap);
	@POST("Member/Bank.ashx")                                                                                                                       // 获取银行列表信息
	//Observable<List<BankInfoGson.DataBean>>getBankInfo();
	Observable<HttpResult<List<BankInfoGson.DataBean>>>getBankInfo();
}
