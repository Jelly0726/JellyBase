package com.base.bottomBar;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/21.
 */

public class CurrentItem implements Serializable{
    private int itemIndex=0;
    private String data;
    private int type=0;
    public CurrentItem(int itemIndex, String data, int type){
        this.itemIndex = itemIndex;
        this.data = data;
        this.type = type;
    }
    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
