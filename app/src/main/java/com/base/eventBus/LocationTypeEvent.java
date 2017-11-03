package com.base.eventBus;

/**
 * Created by BYPC006 on 2016/10/11.
 */
public class LocationTypeEvent {
	public static final int sTYPE_HIGHT_PRECISION = 0;      // 高精度模式
	public static final int sTYPE_LBS = 1;                    // 为低功耗模式lbs
	public static final int sTYPE_GPS = 2;                    // 仅设备模式gps
	private int mType;                                          // 开启模式 0:高精度     1: lbs    2: gps;

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}
}
