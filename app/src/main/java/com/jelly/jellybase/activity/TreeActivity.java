package com.jelly.jellybase.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.base.tree.adapter.IMultipleItem;
import com.base.tree.adapter.SectionExpandHelper;
import com.base.tree.adapter.ViewHolder;
import com.base.tree.bean.BaseItem;
import com.base.tree.bean.GrandSon;
import com.base.tree.bean.Item;
import com.base.tree.bean.Section;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TreeActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<Section> mSectionList;             //数据集合
    private SectionExpandHelper mHelper;            //使用层级树的帮助工具类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree_activity);
        mSectionList = new ArrayList<>();
        findView();
    }

    public void findView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mHelper = new SectionExpandHelper(TreeActivity.this, mRecyclerView, new IMultipleItem() {
            private static final int VIEW_TYPE_TWO = R.layout.tree_two_layout;
            private static final int VIEW_TYPE_SECTION = R.layout.tree_section_layout;
            private static final int VIEW_TYPE_THREE = R.layout.tree_three_layout;

            @Override
            public int getItemLayout(BaseItem baseItem) {
                int layoutId = -1;
                if (baseItem instanceof Section)
                    layoutId = VIEW_TYPE_SECTION;
                else if (baseItem instanceof Item)
                    layoutId = VIEW_TYPE_TWO;
                else if (baseItem instanceof GrandSon)
                    layoutId = VIEW_TYPE_THREE;
                return layoutId;
            }

            @Override
            public boolean isSection(BaseItem baseItem) {
                return baseItem instanceof Section;
            }

            @Override
            public void bindData(final ViewHolder holder, int position, final BaseItem item) {
                CheckBox checkBox = null;
                switch (getItemLayout(item)) {
                    case VIEW_TYPE_SECTION:
                        holder.setText(R.id.text_section, item.getName());
                        holder.setViewOnclick(R.id.text_section, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHelper.expandStateChange(item);
                            }
                        });
                        checkBox = holder.getView(R.id.section_checkbox);
                        checkBox.setChecked(item.isChecked());

                        ((CheckBox)holder.getView(R.id.toggle_section))
                                .setChecked(item.isExpanded());
                        holder.setViewOnclick(R.id.toggle_section, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHelper.expandStateChange(item);
                            }
                        });
                        break;
                    case VIEW_TYPE_TWO:
                        holder.setText(R.id.text_item, item.getName());
                        checkBox = holder.getView(R.id.item_checkbox);
                        checkBox.setChecked(item.isChecked());
                        holder.setViewOnclick(R.id.item_layout, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHelper.expandStateChange(item);
                            }
                        });

                        ((CheckBox)holder.getView(R.id.toggle_section))
                                .setChecked(item.isExpanded());
                        holder.setViewOnclick(R.id.toggle_section, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHelper.expandStateChange(item);
                            }
                        });
                        break;
                    case VIEW_TYPE_THREE:
                        holder.setText(R.id.appName, item.getName());
                        holder.setViewOnclick(R.id.apkIcon, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHelper.expandStateChange(item);
                            }
                        });
                        checkBox = holder.getView(R.id.apk_checkbox);
                        checkBox.setChecked(item.isChecked());
                        break;
                }
                if (checkBox != null)
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHelper.checkStateChange(item);
                        }
                    });
            }
        }, new GridLayoutManager(TreeActivity.this, 1), new Comparator<BaseItem>() {
            @Override
            public int compare(BaseItem o1, BaseItem o2) {
                return (int) -(o1.getSize() - o2.getSize());
            }

        });
        initData();
        mHelper.addAllSection(mSectionList);
    }

    public void initData() {
        for (int i = 0; i < 20; i++) {
            Section sec = new Section();
            sec.setSize(i * 50);
            sec.setName("joee " + i);
            List<Item> itemList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                itemList.add(new Item("fyq +" + j, j));
            }
            for (int j = 0; j < itemList.size(); j++) {
                itemList.get(j).addChildItem(new GrandSon("grandson " + j, j));
                itemList.get(j).addChildItem(new GrandSon("grandson " + j, j));
            }
            sec.setmChildList(itemList);
            mSectionList.add(sec);
        }
    }

}
