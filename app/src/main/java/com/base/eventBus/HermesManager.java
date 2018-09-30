package com.base.eventBus;

import android.content.Intent;

import com.base.MapUtil.LocationTask;
import com.base.appManager.BaseApplication;
import com.jelly.jellybase.server.LocationService;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

public class HermesManager {
    private volatile static List<Object> locationEvent = new ArrayList<>();//定位的event接收器管理
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final HermesManager instance = new HermesManager();
    }

    private HermesManager() {}
    /**
     * 单一实例
     */
    public synchronized static HermesManager getHermesManager() {
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
    public int getEventSize(){
        if (locationEvent!=null) {
            return locationEvent.size();
        }else
            return 0;
    }
    public void addEvent(Object event){
        if (locationEvent!=null)
            if (!locationEvent.contains(event)){
                locationEvent.add(event);
            }
    }
    public void removeEvent(Object event){
        if (locationEvent!=null) {
            locationEvent.remove(event);
            if (getEventSize()<=0){
                Intent intent=new Intent(BaseApplication.getInstance(), LocationService.class);
                BaseApplication.getInstance().stopService(intent);
                LocationTask.getInstance(BaseApplication.getInstance()).onDestroy();
            }
        }
    }
    public void clear(){
        locationEvent.clear();
        locationEvent=null;
    }
}
