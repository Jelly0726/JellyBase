package com.jelly.baselibrary.addressmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/11.
 */

public class Province extends Area implements Serializable{
    private List<City> cities=new ArrayList<>();
    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }


}
