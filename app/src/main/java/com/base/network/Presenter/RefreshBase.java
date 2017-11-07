package com.base.network.Presenter;

import java.util.List;

/**
 * Created by BYPC006 on 2017/6/26.
 */

public interface RefreshBase{
	interface View{
		void refresh_ok();
		void refresh_error();
		void loadMore_ok();
		void loadMore_error();
		void toastShowShort(String message);
		void toastShowLong(String message);
	}
	interface Presenter<T>{
		void getData(final boolean isPullDown);
		List<T> getAdapterData();
	}
}
