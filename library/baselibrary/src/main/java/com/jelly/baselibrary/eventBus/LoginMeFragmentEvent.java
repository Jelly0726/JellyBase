package com.jelly.baselibrary.eventBus;

/**
 * Created by BYPC006 on 2016/10/10.
 */
public class LoginMeFragmentEvent {
	private int mKeyOn;                 // 按键按下:    0:确定;   1:取消

	public LoginMeFragmentEvent(int keyOn){
		mKeyOn = keyOn;
	}
	public int getKeyOn() {
		return mKeyOn;
	}

	public void setKeyOn(int keyOn) {
		mKeyOn = keyOn;
	}
}
