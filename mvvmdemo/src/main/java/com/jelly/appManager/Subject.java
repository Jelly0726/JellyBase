package com.jelly.appManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 被观察者，我们称它为Observable，即可以被观察的东西，有时候还会称之为主题，即Subject
 * @author lijie
 */
public class Subject<T> extends Observable<T>{
    private ExecutorService fixedThreadPool= Executors.newFixedThreadPool(3);
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public void attach(final Observer<T> observer){
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock(); // 获取写锁
                try {
                    if (!observers.contains(observer))
                        observers.add(observer);
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }

    public void detach(final Observer<T> observer){
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                try {
                    observers.remove(observer);
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }
    public void detachAll(){
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                try {
                    for (Observer observer : observers) {
                        observer.onUpdate(Subject.this,"");
                    }
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }
    /**
     * 获取存活的activity数量
     *
     * @author kymjs
     */
    public int getCount() {
        return observers==null?0:observers.size();
    }
    public void notifyObservers(){
        detachAll();
    }
}
