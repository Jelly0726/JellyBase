package com.jelly.jellybase.datamodel;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */

public class RecevierAddress implements Serializable{
    private boolean isDefault=false;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
