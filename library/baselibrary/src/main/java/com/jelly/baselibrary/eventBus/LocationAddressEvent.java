package com.jelly.baselibrary.eventBus;

/**
 * Created by BYPC006 on 2016/10/12.
 */
public class LocationAddressEvent {
	private boolean mIsAddressGet = true;            // 地址定位是否成功

	public boolean isAddressGet() {
		return mIsAddressGet;
	}

	public void setAddressGet(boolean addressGet) {
		mIsAddressGet = addressGet;
	}
}
