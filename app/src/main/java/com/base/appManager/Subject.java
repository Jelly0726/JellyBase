package com.base.appManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Subject {
    private List<Observer> observers = new ArrayList<Observer>();
    private ExecutorService fixedThreadPool= Executors.newFixedThreadPool(3);
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public void attach(final Observer observer){
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                readWriteLock.writeLock().lock(); // 获取写锁
                try {
                    observers.add(observer);
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }

    public void detach(final Observer observer){
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
                        observer.update(Subject.this);
                    }
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        });
    }

    protected void notifyObservers(){
        detachAll();
    }
}
