package com.jelly.jellybase.datamodel;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/1.
 */

public class TopItem implements Serializable{
    private int icon;
    private String img;
    private String title;
    private int id;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
