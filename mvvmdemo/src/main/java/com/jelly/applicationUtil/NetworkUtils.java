package com.jelly.applicationUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;

import java.io.ObjectStreamException;

/**
 * Created by Administrator on 2017/12/4.
 */
public class NetworkUtils {
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final NetworkUtils instance = new NetworkUtils();
    }

    private NetworkUtils() {}
    /**
     * 单一实例
     */
    public synchronized static NetworkUtils getInstance() {
        return SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return SingletonHolder.instance;
    }
//    网络是否可用
    private boolean isAvailable=false;
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    /**
     * 注册网络监听实现
     */
   public void registerNetWorkCallBack(Context context){
       NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();
       NetworkRequest.Builder builder = new NetworkRequest.Builder();
       NetworkRequest request = builder.build();
       ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       if (connMgr != null) {
           connMgr.registerNetworkCallback(request, networkCallback);
       }
   }
}
