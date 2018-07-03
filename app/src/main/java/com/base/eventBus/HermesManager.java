package com.base.eventBus;

import java.util.Stack;

public class HermesManager {
    private Stack<Object> locationEvent=new Stack<Object>();//定位的event接收器管理
    private volatile static HermesManager instance;
    private HermesManager() {}
    /**
     * 单一实例
     */
    public static HermesManager getHermesManager() {
        if (instance == null) {
            synchronized (HermesManager.class) {
                if (instance == null) {
                    instance = new HermesManager();
                }
            }
            return instance;
        }
        return instance;
    }
    public int getEventSize(){
        if (locationEvent!=null)
            return locationEvent.size();
        else
            return 0;
    }
    public void addEvent(Object event){
        if (locationEvent!=null)
            if (!locationEvent.contains(event)){
                locationEvent.add(event);
            }
    }
    public void removeEvent(Object event){
        if (locationEvent!=null)
            locationEvent.remove(event);
    }
    public void clear(){
        locationEvent.clear();
        locationEvent=null;
    }
}
