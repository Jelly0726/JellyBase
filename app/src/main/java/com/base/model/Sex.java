package com.base.model;

/**
 * Created by Administrator on 2018/1/19.
 */

public class Sex {
    private int id;
    private String name;
    public Sex(int id, String name){
        this.id=id;
        this.name=name;
    }
    public int getId() {
        return id;
    }

    public Sex setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Sex setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
