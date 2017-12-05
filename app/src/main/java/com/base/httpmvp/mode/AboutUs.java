package com.base.httpmvp.mode;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/11.
 */

public class AboutUs implements Serializable{
    private String companyname;
    private String address;
    private String telephone;

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
