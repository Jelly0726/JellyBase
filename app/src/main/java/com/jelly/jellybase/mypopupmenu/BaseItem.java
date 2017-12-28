package com.jelly.jellybase.mypopupmenu;

/**
 * Created by Administrator on 2017/9/20.
 */

public class BaseItem {
    public BaseItem(){
    }
    public BaseItem(String title,int id){
        this.title=title;
        this.id=id;
    }
    public String title="";
    public int id=0;
    public boolean isCheck=false;

    public String getTitle() {
        return title;
    }

    public BaseItem setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return id;
    }

    public BaseItem setId(int id) {
        this.id = id;
        return this;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public BaseItem setCheck(boolean check) {
        isCheck = check;
        return this;
    }
}
