package com.jelly.mvp.model;


import android.text.TextUtils;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.baselibrary.model.UploadBean;
import com.jelly.baselibrary.model.UploadData;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalInfoModel extends BaseModel {
    /**
     * 上传文件(图片)
     */
    public void upload(File file, UploadBean uploadBean, ObservableTransformer composer, ObserverResponseListener<HttpResult> listener){
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String type="image";
        if(!TextUtils.isEmpty(uploadBean.getFileType())){
            type=uploadBean.getFileType();
        }
        // MultipartBody.Part  和后端约定好Key
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(type, file.getName(), requestFile);
        // 添加描述
        String descriptionString =uploadBean.getFileDesc();
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);
        // 执行请求
        Observable observable = HttpMethods.getInstance().getProxy(IApiService.class).upload(GlobalToken.getToken().getToken(),
                description, body)
                .flatMap(new HttpFunctions<HttpResultData<UploadData>>());
        subscribe(observable,composer,listener);
    }
}
