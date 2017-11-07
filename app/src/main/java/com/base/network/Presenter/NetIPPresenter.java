package com.base.network.Presenter;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.base.network.DataBean.NetIPJson;
import com.base.network.Mode.NetIPSourceInter;
import com.base.network.Mode.NetIPSourceMode;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by BYPC006 on 2017/6/23.
 */

public class NetIPPresenter implements NetIPInter.Presenter{
	public static final String TAG = NetIPPresenter.class.getName();
	public static final int sHANDLER_PARAM_ORDERS_GET_ERROR = 0;                           // handler消息处理

	private int mPageIndex = 1;
	private int mPageSize = 5;

	private final NetIPInter.View mTaskView;
	private final NetIPSourceMode mTasksRepository;
	private final List<NetIPJson.DataBean> mData = new ArrayList<>();

	public NetIPPresenter(@NonNull NetIPInter.View tasksView, @NonNull NetIPSourceMode tasksRespository){
//		mTaskView = checkNotNull(tasksView, "tasksView cannot be null!");
//		mTasksRepository = checkNotNull(tasksRespository, "tasksRespository cannot be null!");
		mTaskView = tasksView;
		mTasksRepository = tasksRespository;
	}

	@Override
	public void getData(final boolean isPullDown) {
		if (!isPullDown){
			mPageIndex++;
		}else {
			mPageIndex = 1;
		}
		mTasksRepository.getData(new NetIPSourceInter.GetDataCallBack() {
			@Override
			public void onError(String message) {
				if (mPageIndex > 1){
					mPageIndex --;
				}
				Message msg = Message.obtain();
				msg.what = sHANDLER_PARAM_ORDERS_GET_ERROR;
				msg.obj = message;
				mHandler.sendMessage(msg);
				if (isPullDown){
					mTaskView.refresh_error();
				}else {
					mTaskView.loadMore_error();
				}
			}

			@Override
			public void onNext(NetIPJson.DataBean dataBeen) {
				if (isPullDown){
					mData.clear();
				}
					mData.add(dataBeen);
				//数据处理
				if (isPullDown){
					mTaskView.refresh_ok();
				}else {
					mTaskView.loadMore_ok();
				}
			}
		});
	}

	@Override
	public List<NetIPJson.DataBean> getAdapterData() {
		return mData;
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case sHANDLER_PARAM_ORDERS_GET_ERROR:
					mTaskView.toastShowShort((String)msg.obj);
					break;
				default:
					break;
			}
		}
	};
}
