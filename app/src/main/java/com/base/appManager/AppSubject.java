package com.base.appManager;
/**
 * 使用观察者模式完美实现android程序退出
 */
public class AppSubject extends Subject {
    private volatile static AppSubject instance;
    private AppSubject() {}
    /**
     * 单一实例
     */
    public static AppSubject getAppSubject() {
        if (instance == null) {
            synchronized (AppSubject.class) {
                if (instance == null) {
                    instance = new AppSubject();
                }
            }
            return instance;
        }
        return instance;
    }
    public void exit(){
        MyApplication.getMyApp().exit();
        notifyObservers();
    }
}