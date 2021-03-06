package com.jelly.mvp.model;


import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.jelly.baselibrary.model.UploadBean;
import com.jelly.baselibrary.model.UploadData;
import com.jelly.baselibrary.token.GlobalToken;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalInfoModel extends BaseModel {
    /**
     * 上传文件(图片)
     */
    public void upload(File file, UploadBean uploadBean, LifecycleOwner composer, ObserverResponseListener<HttpResultData<UploadData>> listener){
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile =
                RequestBody.create(file,MediaType.parse("multipart/form-data"));
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
                RequestBody.create(descriptionString,
                        MediaType.parse("multipart/form-data"));
        // 执行请求
        Observable observable = HttpMethods.getInstance().getProxy(IApiService.class).upload(GlobalToken.getToken().getToken(),
                description, body)
                .flatMap(new HttpFunctions<HttpResultData<UploadData>>());
        subscribe(observable,composer,listener);
    }
}
