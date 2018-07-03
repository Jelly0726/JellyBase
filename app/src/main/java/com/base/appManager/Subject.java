package com.base.appManager;

import java.util.Stack;

public class Subject {
    private Stack<Observer> observers = new Stack<Observer>();

    public void attach(Observer observer){
        observers.add(observer);
    }

    public void detach(Observer observer){
        observers.remove(observer);
    }
    public void detachAll(){
        for(Observer observer : observers){
            observer.update(this);
        }
    }

    protected void notifyObservers(){
        for(Observer observer : observers){
            observer.update(this);
        }
    }
}
