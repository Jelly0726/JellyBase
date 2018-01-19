package com.base.tree.adapter;


import com.base.tree.bean.BaseItem;

/**
 * Created by joee on 2016/9/7.
 */
public interface SectionStateChangeListener {
    public void SectionStateChange(BaseItem section, boolean isChecked);

    public void expandStateChange(BaseItem section);

    public void checkStateChange(BaseItem baseItem);
}
