package com.base.appManager;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private List<Observer> observers = new ArrayList<Observer>();
    private static boolean isOver=true;
    public void attach(Observer observer){
        if (isOver) {
            isOver=false;
            observers.add(observer);
            isOver=true;
        }
    }

    public void detach(Observer observer){
        if (isOver) {
            isOver=false;
            observers.remove(observer);
            isOver=true;
        }
    }
    public void detachAll(){
        if (isOver) {
            isOver=false;
            for (Observer observer : observers) {
                observer.update(this);
            }
            observers.clear();
            isOver=true;
        }
    }

    protected void notifyObservers(){
        if (isOver) {
            isOver=false;
            for (Observer observer : observers) {
                observer.update(this);
            }
            observers.clear();
            isOver=true;
        }
    }
}
