package com.jelly.baselibrary.appManager;

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

    /**
     * 添加 observer
     * @param observer
     */
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

    /**
     * 移除 observer
     * @param observer
     */
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

    /**
     * 移除对应下标的observer
     * @param index
     */
    public void detachAt(int index){
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                try{
                    if (index<observers.size()){
                        Observer observer=observers.get(index);
                        observer.onUpdate(Subject.this,"");
                        observers.remove(index);
                    }
                }finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }
    /**
     *
     *  移除近几个observer
     * @param num 移除数量
     */
    public void detachFor(int num){
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                try{
                    for (int i = 1;i<=num;i++){
                        int index=observers.size()-i;
                        Observer observer=observers.get(index);
                        observer.onUpdate(Subject.this,"");
                        observers.remove(index);
                    }
                }finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }
    /**
     *移除此observer之前的几个observer
     * @param observer
     * @param num 移除数量
     */
    public void detachFor(final Observer<T> observer,int num){
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                try{
                    //循环移除
                    for (int i = 0;i<num;i++){
                        //每次移除当前的前一个
                        int index=observers.indexOf(observer)-1;
                        Observer observer=observers.get(index);
                        observer.onUpdate(Subject.this,"");
                        observers.remove(index);
                    }
                }finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }
    /**
     * 移除全部
     */
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
     */
    public int getCount() {
        return observers==null?0:observers.size();
    }
    public void notifyObservers(){
        detachAll();
    }
}
