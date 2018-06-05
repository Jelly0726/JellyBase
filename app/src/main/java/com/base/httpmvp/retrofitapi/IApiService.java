package com.base.httpmvp.retrofitapi;


import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.databean.AboutUs;
import com.base.httpmvp.databean.AccountDetail;
import com.base.httpmvp.databean.AppVersion;
import com.base.httpmvp.databean.Message;
import com.base.httpmvp.databean.PersonalInfo;
import com.base.httpmvp.databean.UploadData;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.jelly.jellybase.datamodel.RecevierAddress;

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
	Observable<HttpResultData<TokenModel>> getToken(@Query("data") Object jsonObject);
	//@GET("request")
	//Observable<ResultModel> getResult(@Query("token") String token);
	//登录
	@POST("SLD/buyers/login.doAdminJJ")
	Observable<HttpResultData<Login>> userLogin(@Query("data") Object jsonObject);
	//获取验证码
	@POST("SLD/buyers/register1.doAdminJJ")
	Observable<HttpResult> getVerifiCode(@Query("data") Object jsonObject);
	//注册
	@POST("SLD/buyers/register2.doAdminJJ")
	Observable<HttpResult> userRegistration(@Query("data") Object jsonObject);
	//设置密码
	@POST("SLD/buyers/addPassword.doAdminJJ")
	Observable<HttpResult> setPassWord(@Query("data") Object jsonObject);
	//忘记密码
	@POST("SLD/buyers/forgetPassword.doAdminJJ")
	Observable<HttpResult> forgetPassword(@Query("data") Object jsonObject);
	//修改密码
	@POST("SLD/buyersInfo/updatePassword.doAdminJJ")
	Observable<HttpResult> updatePassword(@Header("token") String token,
                                                         @Query("data") Object jsonObject);
	//修改手机号
	@POST("SLD/buyersInfo/updatePhone.doAdminJJ")
	Observable<HttpResult> updatePhone(@Header("token") String token,
                                                      @Query("data") Object jsonObject);
	//关于我们
	@POST("SLD/buyersInfo/aboutUs.doAdminJJ")
	Observable<HttpResultData<AboutUs>> aboutUs(@Header("token") String token);
	//检查版本
	@POST("JFF/appuser/getAppversionList.doAdminJJ")
	Observable<HttpResultData<AppVersion>> getAppversionList(@Header("token") String token);
	//获取个人资料
	@POST("zhongbo/user/getMeuserList.doAdminJJ")
	Observable<HttpResultData<PersonalInfo>> findBuyerInfo(@Header("Authorization") String token);
	//完善个人资料
	@POST("zhongbo/user/operateMeuser.doAdminJJ")
	Observable<HttpResult> updateBuyerInfo(@Header("Authorization") String token,
										   @Query("data") Object jsonObject);

	//获取所属银行
	@POST("SLD/buyersInfo/getBankLogo.doAdminJJ")
	Observable<HttpResultData<BankCardInfo>> getBank(@Header("token") String token,
                                                                    @Query("data") Object jsonObject);
	//上传文件
	@Multipart
	@POST("SLD/buyersInfo/uploadAttachment.doAdminJJ")
	Observable<HttpResultData<UploadData>> upload(@Header("token") String token,
                                                                 @Part("description") RequestBody description,
                                                                 @Part MultipartBody.Part file);
	//银行卡列表
	@POST("SLD/buyersInfo/bankList.doAdminJJ")
	Observable<HttpResultList<BankCardInfo>> bankList(@Header("token") String token,
                                                                     @Query("data") Object jsonObject);
	//添加银行卡
	@POST("SLD/buyersInfo/addbank.doAdminJJ")
	Observable<HttpResult> addbank(@Header("token") String token, @Query("data") Object jsonObject);
	//删除银行卡
	@POST("SLD/buyersInfo/deleteBankById.doAdminJJ")
	Observable<HttpResult> deletebank(@Header("token") String token, @Query("data") Object jsonObject);
	//提现
	@POST("SLD/buyersInfo/withdrawals.doAdminJJ")
	Observable<HttpResult> withdrawals(@Header("token") String token, @Query("data") Object jsonObject);
	//账户明细
	@POST("SLD/buyersInfo/buyerAccountDetails.doAdminJJ")
	Observable<HttpResultList<AccountDetail>> accountDetails(@Header("token") String token,
																			@Query("data") Object jsonObject);
	//配置地址
	@POST("SLD/buyerAddress/operaAddress.doAdminJJ")
	Observable<HttpResult> operaAddress(@Header("token") String token,
													   @Query("data") Object jsonObject);
	//获取地址列表
	@POST("SLD/buyerAddress/getAddressList.doAdminJJ")
	Observable<HttpResultList<RecevierAddress>> getAddressList(@Header("token") String token,
																			  @Query("data") Object jsonObject);
	//消息通知列表
	@POST("JFF/fish/transfer/getFishTransferList.doAdminJJ")
	Observable<HttpResultList<Message>> getMessage(@Header("token") String token,
																  @Query("data") Object jsonObject);
	//消息通知详情
	@POST("JFF/fish/transfer/getFishTransferList.doAdminJJ")
	Observable<HttpResultData<Message>> getMessageDetails(@Header("token") String token,
																		 @Query("data") Object jsonObject);
}
