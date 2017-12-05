package com.base.httpmvp.databean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/1.
 */

public class AppVersion implements Serializable{
    private int appversion;
    private String url;
    private String IP;

    public int getAppversion() {
        return appversion;
    }

    public void setAppversion(int appversion) {
        this.appversion = appversion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}
