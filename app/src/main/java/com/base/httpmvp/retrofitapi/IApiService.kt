package com.base.httpmvp.retrofitapi

import com.base.httpmvp.retrofitapi.methods.HttpResult
import com.base.httpmvp.retrofitapi.methods.HttpResultData
import com.base.httpmvp.retrofitapi.methods.HttpResultJson
import com.base.httpmvp.retrofitapi.methods.HttpResultList
import com.jelly.baselibrary.bankcard.BankCardInfo
import com.jelly.baselibrary.model.*
import com.jelly.baselibrary.token.TokenModel
import com.jelly.jellybase.datamodel.RecevierAddress
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*
import systemdb.Login

/**
 * Created by BYPC006 on 2017/3/6.
 * url被转义:http://api.mydemo.com/api%2Fnews%2FnewsList?
 *
 * @POST()
 * Call<HttpResult></HttpResult><News>> post(@Url String url, @QueryMap Map<String></String>, String> map);
 * @POST("api/{url}/newsList")
 * Call<HttpResult></HttpResult><News>> login(@Path("url") String url, @Body News post);
 *
 * 第一种是直接使用@Url,它相当于直接替换了@POST()里面的请求地址
 * 第二种是使用@Path("url"),它只替换了@POST("api/{url}/newsList")中的{url}
 * 如果你用下面这样写的话,就会出现url被转义
 * @POST("{url}")
 * Call<HttpResult></HttpResult><News>> post(@Path("url") String url);
 * 你如果执意要用@Path,也不是不可以,需要这样写
 * @POST("{url}")
 * Call<HttpResult></HttpResult><News>> post(@Path(value = "url", encoded = true) String url);
 * @其他声明
 * @请求方式("请求地址")
 * Observable<请求返回的实体> 请求方法名(请求参数)；
 * 请求地址，填写基础请求路径baseUrl后续的部分即可，当然填写完整地址也是可以的
 * 或者
 * @其他声明
 * @请求方式
 * Observable<请求返回的实体> 请求方法名(@Url String 请求地址，请求参数)；
 * 请求地址，需填写完整的地址。
</请求返回的实体></请求返回的实体></News></News></News></News> */
interface IApiService {
    //发送崩溃信息
    @POST
    fun sendError(@Url url: String, @Body jsonString: Map<*, *>): Observable<HttpResultJson>

    //获取token
    @FormUrlEncoded
    @GET("SLD/token/getToken.doAdminJJ")
    fun getToken(@Field("data") jsonString: String?): Observable<HttpResultData<TokenModel>>

    //@GET("request")
    //Observable<ResultModel> getResult(@Field("token") String token);
    //登录
    @FormUrlEncoded
    @POST("SLD/buyers/login.doAdminJJ")
    fun userLogin(@Field("data") jsonString: String?): Observable<HttpResultData<Login>>

    //获取验证码
    @FormUrlEncoded
    @POST("SLD/buyers/register1.doAdminJJ")
    fun getVerifiCode(@Field("data") jsonString: String?): Observable<HttpResult>

    //注册
    @FormUrlEncoded
    @POST("SLD/buyers/register2.doAdminJJ")
    fun userRegistration(@Field("data") jsonString: String?): Observable<HttpResult>

    //设置密码
    @FormUrlEncoded
    @POST("SLD/buyers/addPassword.doAdminJJ")
    fun setPassWord(@Field("data") jsonString: String?): Observable<HttpResult>

    //忘记密码
    @FormUrlEncoded
    @POST("SLD/buyers/forgetPassword.doAdminJJ")
    fun forgetPassword(@Field("data") jsonString: String?): Observable<HttpResult>

    //修改密码
    @FormUrlEncoded
    @POST("SLD/buyersInfo/updatePassword.doAdminJJ")
    fun updatePassword(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //修改手机号
    @FormUrlEncoded
    @POST("SLD/buyersInfo/updatePhone.doAdminJJ")
    fun updatePhone(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //关于我们
    @POST("SLD/buyersInfo/aboutUs.doAdminJJ")
    fun aboutUs(@Header("token") token: String?): Observable<HttpResultData<AboutUs>>

    //检查版本
    @POST("JFF/appuser/getAppversionList.doAdminJJ")
    fun getAppversionList(@Header("token") token: String?): Observable<HttpResultData<AppVersion>>

    //获取个人资料
    @POST("zhongbo/user/getMeuserList.doAdminJJ")
    fun findBuyerInfo(@Header("Authorization") token: String?): Observable<HttpResultData<PersonalInfo>>

    //完善个人资料
    @FormUrlEncoded
    @POST("zhongbo/user/operateMeuser.doAdminJJ")
    fun updateBuyerInfo(
        @Header("Authorization") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //上传文件
    @Multipart
    @POST("SLD/buyersInfo/uploadAttachment.doAdminJJ")
    fun upload(
        @Header("token") token: String?,
        @Part("description") description: RequestBody?,
        @Part file: Part?
    ): Observable<HttpResultData<UploadData>>

    //银行卡列表
    @FormUrlEncoded
    @POST("SLD/buyersInfo/bankList.doAdminJJ")
    fun bankList(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResultList<BankCardInfo>>

    //添加银行卡
    @FormUrlEncoded
    @POST("SLD/buyersInfo/addbank.doAdminJJ")
    fun addbank(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //删除银行卡
    @FormUrlEncoded
    @POST("SLD/buyersInfo/deleteBankById.doAdminJJ")
    fun deletebank(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //提现
    @FormUrlEncoded
    @POST("SLD/buyersInfo/withdrawals.doAdminJJ")
    fun withdrawals(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //账户明细
    @FormUrlEncoded
    @POST("SLD/buyersInfo/buyerAccountDetails.doAdminJJ")
    fun accountDetails(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResultList<AccountDetail>>

    //配置地址
    @FormUrlEncoded
    @POST("SLD/buyerAddress/operaAddress.doAdminJJ")
    fun operaAddress(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResult>

    //获取地址列表
    @FormUrlEncoded
    @POST("SLD/buyerAddress/getAddressList.doAdminJJ")
    fun getAddressList(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResultList<RecevierAddress>>

    //消息通知列表
    @FormUrlEncoded
    @POST("JFF/fish/transfer/getFishTransferList.doAdminJJ")
    fun getMessage(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResultList<Message>>

    //消息通知详情
    @FormUrlEncoded
    @POST("JFF/fish/transfer/getFishTransferList.doAdminJJ")
    fun getMessageDetails(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResultData<Message>>

    //取消订单
    @FormUrlEncoded
    @POST("JFF/fish/transfer/cancelOrder.doAdminJJ")
    fun cancelOrder(
        @Header("token") token: String?,
        @Field("data") jsonString: String?
    ): Observable<HttpResultData<HttpResult>>
}