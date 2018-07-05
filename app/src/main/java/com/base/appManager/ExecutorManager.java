package com.base.appManager;

import java.io.ObjectStreamException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池管理工具
 */
public class ExecutorManager {
    private static final int MAX_Thread=2;//定长线程池的最大并发数
    private static ExecutorService cachedThreadPool;//可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
    private static ExecutorService fixedThreadPool ;//创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
    private static ScheduledExecutorService scheduledThreadPool;//定长线程池，支持定时及周期性任务执行
    private static ExecutorService singleThreadExecutor;//单线线程池 线程会在队列中等待
    private static ExecutorService getThread;//线程池

    private ExecutorManager(){
    };
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final ExecutorManager instance=new ExecutorManager();
    }
    /**
     * 单一实例
     */
    public static ExecutorManager getExecutorManager(){
        return SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    public Object readResolve() throws ObjectStreamException{
        return SingletonHolder.instance;
    }
    /**
     * 可缓存线程池
     * @return
     */
    public ExecutorService getCachedThread() {
        /*
        例代码如下：
        cachedThreadPool.execute(runnable)
         */
        if(cachedThreadPool==null){
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        return cachedThreadPool;
    }

    /**
     * 定长线程池，可控制线程最大并发数
     * @return
     */
    public ExecutorService getFixedThread() {
         /*
        例代码如下：
        fixedThreadPool.execute(runnable)
        */
        if(fixedThreadPool==null){
            fixedThreadPool = Executors.newFixedThreadPool(MAX_Thread);
        }
        return fixedThreadPool;
    }
    /**
     *定长线程池，支持定时及周期性任务执行
     */
    public ExecutorService getScheduledThread() {

        /*
         * 延迟执行示例代码如下：
         * scheduledThreadPool.schedule(runnable, 3, TimeUnit.SECONDS);
         表示延迟3秒执行
         * 定期执行示例代码如下：
         scheduledThreadPool.scheduleAtFixedRate(runnable, 1, 3, TimeUnit.SECONDS);
         表示延迟1秒后每3秒执行一次。
         */
        if(scheduledThreadPool==null){
            scheduledThreadPool = Executors.newScheduledThreadPool(MAX_Thread);
        }
        return scheduledThreadPool;
    }

    /**
     * 单线线程池 线程会在队列中等待
     * @return
     */
    public ExecutorService getSingleThread() {
        /*
        例代码如下：
        singleThreadExecutor.execute(runnable)
         */
        if(singleThreadExecutor==null){
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        return singleThreadExecutor;
    }
    public ExecutorService getThread() {
        if(getThread==null){
            getThread = Executors.newCachedThreadPool();
        }
        return getThread;
    }
}
