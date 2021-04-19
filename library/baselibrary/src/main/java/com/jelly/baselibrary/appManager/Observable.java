package com.jelly.baselibrary.appManager;

import java.util.ArrayList;
import java.util.List;
/**
 * 被观察者，我们称它为Observable，即可以被观察的东西，有时候还会称之为主题，即Subject
 * @author lijie
 */
public class Observable<T> {
    List<Observer<T>> observers = new ArrayList<Observer<T>>();
    public void register(Observer<T> observer) {
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        synchronized (this) {
            if (!isRegistered(observer))
                observers.add(observer);
        }
    }

    public synchronized void unregister(Observer<T> observer) {
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        observers.remove(observer);
    }
    public boolean isRegistered(Observer<T> observer){
        return observers.contains(observer);
    }
    public boolean isRegistered(Class<?> clas){
        for (Observer<T> observer : observers) {
            if (observer.getClass().equals(clas)){
                return true;
            }
        }
        return false;
    }

    public void notifyObservers(T data) {
        for (Observer<T> observer : observers) {
            observer.onUpdate(this, data);
        }
    }

}