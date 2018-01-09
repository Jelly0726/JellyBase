package com.jelly.jellybase.datamodel;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */

public class RecevierAddress implements Serializable{
    private boolean isdefault =false;
    private int addressid=0;
    private String type;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String area;
    private String address;
    public boolean isDefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public int getAddressid() {
        return addressid;
    }

    public void setAddressid(int addressid) {
        this.addressid = addressid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
