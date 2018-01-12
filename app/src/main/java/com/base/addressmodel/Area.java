package com.base.addressmodel;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/11.
 */

public class Area implements Serializable{
    private int areaId;
    private String areaName;
    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
