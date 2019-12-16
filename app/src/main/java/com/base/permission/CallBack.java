package com.base.permission;

import java.util.List;

public interface CallBack {
    public void onSucess();
    public void onFailure(List<String> permissions);
}
