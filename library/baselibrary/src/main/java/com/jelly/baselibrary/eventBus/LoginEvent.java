package com.jelly.baselibrary.eventBus;

/**
 * Created by BYPC006 on 2016/11/30.
 */
public class LoginEvent {
	private boolean mIsLoginIn;

	public LoginEvent(boolean isLoginIn){
		mIsLoginIn = isLoginIn;
	}

	public boolean isLoginIn() {
		return mIsLoginIn;
	}
	public void setLoginIn(boolean loginIn) {
		mIsLoginIn = loginIn;
	}
}
