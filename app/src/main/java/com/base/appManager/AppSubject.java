package com.base.appManager;

import java.io.ObjectStreamException;

/**
 * 使用观察者模式完美实现android程序退出
 */
public class AppSubject extends Subject {
    private static int count = 0;
    /**
     * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
     * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
     * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
     */
    private AppSubject() {
        /**
         * 通过反射获得单例类的构造函数
         * 抵御这种攻击，要防止构造函数被成功调用两次。需要在构造函数中对实例化次数进行统计，大于一次就抛出异常。
         */
        synchronized (AppSubject.class) {
            if(count > 0){
                throw new RuntimeException("创建了两个实例");
            }
            count++;
        }
    }
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final AppSubject instance = new AppSubject();
    }
    /**
     * 单一实例
     */
    public static AppSubject getInstance() {
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
    public void exit(){
        BaseApplication.getInstance().exit();
        notifyObservers();
    }
}