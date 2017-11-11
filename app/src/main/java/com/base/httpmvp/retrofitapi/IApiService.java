package com.base.httpmvp.retrofitapi;


import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.token.TokenModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public interface IApiService {
	//获取token
	@GET("SLD/token/getToken.doAdminJJ")
	Observable<HttpStateData<HttpResultData<TokenModel>>> getToken(@Query("data") Object jsonObject);
	//@GET("request")
	//Observable<ResultModel> getResult(@Query("token") String token);
	//注册
	@POST("SLD/sales/register2.doAdminJJ")
	Observable<HttpStateData<List<HttpResult>>> userRegistration(@Query("data") Object jsonObject);

	//上传文件
	@Multipart
	@POST("SLD/sales/uploadAttachment.doAdminJJ")
	Observable<HttpStateData<HttpResultData<UploadData>>> upload(@Header("token") String token,
																 @Part("description") RequestBody description,
																 @Part MultipartBody.Part file);
}
