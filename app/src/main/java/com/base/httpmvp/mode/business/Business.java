package com.base.httpmvp.mode.business;

import com.alibaba.fastjson.JSON;
import com.base.httpmvp.mode.databean.RegistrationVo;
import com.base.httpmvp.retrofitapi.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Subscriber;

/**
 * Created by Administrator on 2017/11/8.
 */

public class Business implements IBusiness {

    private static final String TAG = Business.class.getSimpleName();

    @Override
    public void register(Object mUserVo, final ICallBackListener mICallBackListener) {
        JSONObject paramMap = new JSONObject();
        //paramMap.put("token",GlobalToken.getToken());
        try {
            paramMap.put("account","18850221236");
            paramMap.put("password","C4CA4238A0B923820DCC509A6F75849B");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpMethods.getInstance().userRegistration(JSON.toJSON(mUserVo),new Subscriber<List<RegistrationVo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onNext(List<RegistrationVo> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }

    @Override
    public void login(Object obj, ICallBackListener mICallBackListener) {
    }

    @Override
    public void forgetPwd(Object obj, ICallBackListener mICallBackListener) {
        // TODO
    }

    @Override
    public void feedBack(Object obj, ICallBackListener mICallBackListener) {
        // TODO
    }
}
