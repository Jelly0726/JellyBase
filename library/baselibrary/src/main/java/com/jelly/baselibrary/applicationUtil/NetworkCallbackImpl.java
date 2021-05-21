package com.jelly.baselibrary.applicationUtil;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.jelly.baselibrary.log.LogUtils;

/**
 * 监听网络是否可用
 */
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        LogUtils.i("网络已链接");
        NetworkUtils.getInstance().setAvailable(true);
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        LogUtils.i("网络已断开");
        NetworkUtils.getInstance().setAvailable(false);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                LogUtils.i("wifi已经连接");
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                LogUtils.i("数据流量已经连接");
            } else {
                LogUtils.i("其他网络");
            }
        }
    }
}