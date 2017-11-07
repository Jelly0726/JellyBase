package com.base.http.Mode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.base.http.DataBean.NetIPJson;
import com.base.http.RetrofitApi.HttpMethods;

import org.greenrobot.greendao.annotation.NotNull;

import rx.Subscriber;
import systemdb.Login;

/**
 * Created by BYPC006 on 2017/6/23.
 */

public class NetIPSourceMode implements NetIPSourceInter {
	private Login mUser;
	private Long mUserId=0l;


	public static NetIPSourceMode newInstance(@NotNull Context context){
		NetIPSourceMode instance = new NetIPSourceMode(context);
		return instance;
	}

	private NetIPSourceMode(@NonNull Context context){

	}
	@Override
	public Long getUserId() {
		if (mUserId == 0l){
			mUserId = mUser.getId();
		}
		return mUserId;
	}

	@Override
	public void getData(final GetDataCallBack callBack) {
		Subscriber<NetIPJson.DataBean> subscriber = new Subscriber<NetIPJson.DataBean>() {
			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable e) {
				Log.i("SS","e="+e);
				callBack.onError(e.getMessage());
			}

			@Override
			public void onNext(NetIPJson.DataBean dataBeen) {
				callBack.onNext(dataBeen);
			}
		};
		HttpMethods.getInstance().getNetIP(subscriber);
	}
}
