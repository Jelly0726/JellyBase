package com.jelly.baselibrary.addressmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/11.
 */

public class Area implements Serializable{
    @SerializedName(value = "areaId",alternate = "region_id")
    private int areaId;//编码
    @SerializedName(value = "areaName",alternate = "region_name")
    private String areaName;//名称
    @SerializedName(value = "region_parentid")
    private int regionParentId;//上级编码
    @SerializedName(value = "region_type")
    private int regionType;//城市类型（省、市、区、县、镇、村、街道）
    @SerializedName(value = "region_is_open")
    private boolean regionIsOpen=false;//是否开放

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

    public int getRegionParentId() {
        return regionParentId;
    }

    public void setRegionParentId(int regionParentId) {
        this.regionParentId = regionParentId;
    }

    public int getRegionType() {
        return regionType;
    }

    public void setRegionType(int regionType) {
        this.regionType = regionType;
    }

    public boolean isRegionIsOpen() {
        return regionIsOpen;
    }

    public void setRegionIsOpen(boolean regionIsOpen) {
        this.regionIsOpen = regionIsOpen;
    }
}
