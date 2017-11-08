package com.base.httpmvp.retrofitapi;


import com.base.httpmvp.mode.databean.RegistrationVo;
import com.base.httpmvp.mode.databean.TokenModel;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
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
	//注册
	@POST("SLD/sales/register2.doAdminJJ")
	Observable<HttpResult<List<RegistrationVo>>> userRegistration(@Query("data") Object jsonObject);
}
