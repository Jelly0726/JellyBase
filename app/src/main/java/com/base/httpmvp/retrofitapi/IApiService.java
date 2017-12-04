package com.base.httpmvp.retrofitapi;


import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.mode.databean.AboutUs;
import com.base.httpmvp.mode.databean.AppVersion;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.token.TokenModel;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import systemdb.Login;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public interface IApiService {
	//获取token
	@GET("SLD/token/getToken.doAdminJJ")
	Observable<HttpStateData<HttpResultData<TokenModel>>> getToken(@Query("data") Object jsonObject);
	//@GET("request")
	//Observable<ResultModel> getResult(@Query("token") String token);
	//登录
	@POST("SLD/buyers/login.doAdminJJ")
	Observable<HttpStateData<HttpResultData<Login>>> userLogin(@Query("data") Object jsonObject);
	//获取验证码
	@POST("SLD/buyers/register1.doAdminJJ")
	Observable<HttpStateData<HttpResult>> getVerifiCode(@Query("data") Object jsonObject);
	//注册
	@POST("SLD/buyers/register2.doAdminJJ")
	Observable<HttpStateData<HttpResult>> userRegistration(@Query("data") Object jsonObject);
	//设置密码
	@POST("SLD/buyers/addPassword.doAdminJJ")
	Observable<HttpStateData<HttpResult>> setPassWord(@Query("data") Object jsonObject);
	//忘记密码
	@POST("SLD/buyers/forgetPassword.doAdminJJ")
	Observable<HttpStateData<HttpResult>> forgetPassword(@Query("data") Object jsonObject);
	//修改密码
	@POST("SLD/buyersInfo/updatePassword.doAdminJJ")
	Observable<HttpStateData<HttpResult>> updatePassword(@Header("token") String token,
                                                         @Query("data") Object jsonObject);
	//修改手机号
	@POST("SLD/buyersInfo/updatePhone.doAdminJJ")
	Observable<HttpStateData<HttpResult>> updatePhone(@Header("token") String token,
                                                      @Query("data") Object jsonObject);
	//关于我们
	@POST("SLD/buyersInfo/aboutUs.doAdminJJ")
	Observable<HttpStateData<HttpResultData<AboutUs>>> aboutUs(@Header("token") String token);
	//检查版本
	@POST("JFF/appuser/getAppversionList.doAdminJJ")
	Observable<HttpStateData<HttpResultData<AppVersion>>> getAppversionList(@Header("token") String token);

	//获取所属银行
	@POST("SLD/buyersInfo/getBankLogo.doAdminJJ")
	Observable<HttpStateData<HttpResultData<BankCardInfo>>> getBank(@Header("token") String token,
                                                                    @Query("data") Object jsonObject);
	//上传文件
	@Multipart
	@POST("SLD/buyersInfo/uploadAttachment.doAdminJJ")
	Observable<HttpStateData<HttpResultData<UploadData>>> upload(@Header("token") String token,
                                                                 @Part("description") RequestBody description,
                                                                 @Part MultipartBody.Part file);
	//银行卡列表
	@POST("SLD/buyersInfo/bankList.doAdminJJ")
	Observable<HttpStateData<HttpResultList<BankCardInfo>>> bankList(@Header("token") String token,
                                                                     @Query("data") Object jsonObject);
	//添加银行卡
	@POST("SLD/buyersInfo/addbank.doAdminJJ")
	Observable<HttpStateData<HttpResult>> addbank(@Header("token") String token, @Query("data") Object jsonObject);
}
