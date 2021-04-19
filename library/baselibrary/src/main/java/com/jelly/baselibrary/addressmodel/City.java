package com.jelly.baselibrary.addressmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/11.
 */

public class City extends Area implements Serializable{
    private List<Area> counties=new ArrayList<>();

    public List<Area> getCounties() {
        return counties;
    }

    public void setCounties(List<Area> counties) {
        this.counties = counties;
    }
}
