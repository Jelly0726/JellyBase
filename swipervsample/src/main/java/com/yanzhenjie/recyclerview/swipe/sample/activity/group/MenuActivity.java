/*
 * Copyright 2017 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.recyclerview.swipe.sample.activity.group;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.activity.stick.StickAdapter;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * <p>
 * Sticky Item + Menu.
 * </p>
 * Created by YanZhenjie on 2017/7/22.
 */
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xswipe_group_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        SwipeRecyclerView recyclerView = (SwipeRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);

        GroupAdapter adapter = new GroupAdapter();
        recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.xswipe_divider_color)));
//        recyclerView.addItemDecoration(StickItemDecoration.builder().adapterProvider(adapter)
//                .build());

        recyclerView.setAdapter(adapter);
        adapter.setListItems(createDataList());
    }

    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (viewType == GroupAdapter.VIEW_TYPE_NON_STICKY) {
                int width = getResources().getDimensionPixelSize(R.dimen.xswipe_dp_70);

                // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
                // 2. 指定具体的高，比如80;
                // 3. WRAP_CONTENT，自身高度，不推荐;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;

                SwipeMenuItem closeItem = new SwipeMenuItem(MenuActivity.this)
                        .setBackground(R.drawable.xswipe_selector_purple)
                        .setImage(R.drawable.xswipe_action_close)
                        .setWidth(width)
                        .setHeight(height);
                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
                swipeRightMenu.addMenuItem(closeItem); // 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(MenuActivity.this)
                        .setBackground(R.drawable.xswipe_selector_green)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    private static class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> implements StickAdapter<GroupViewHolder> {

        static final int VIEW_TYPE_NON_STICKY = R.layout.xswipe_menu_main_item;
        static final int VIEW_TYPE_STICKY = R.layout.xswipe_menu_sticky_item;

        private List<ListItem> mListItems = new ArrayList<>();

        @Override
        public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(viewType, parent, false);
            return new GroupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupViewHolder holder, int position) {
            holder.bind(mListItems.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            if (mListItems.get(position) instanceof StickyListItem) {
                return VIEW_TYPE_STICKY;
            }
            return VIEW_TYPE_NON_STICKY;
        }

        @Override
        public int getItemCount() {
            return mListItems.size();
        }

        @Override
        public int getHeaderCount() {
            return 0;
        }

        void setListItems(List<String> newItems) {
            mListItems.clear();
            for (String item : newItems) {
                mListItems.add(new ListItem(item));
            }

            Collections.sort(mListItems, new Comparator<ListItem>() {
                @Override
                public int compare(ListItem o1, ListItem o2) {
                    return o1.text.compareToIgnoreCase(o2.text);
                }
            });

            StickyListItem stickyListItem = null;
            for (int i = 0, size = mListItems.size(); i < size; i++) {
                ListItem listItem = mListItems.get(i);
                String firstLetter = String.valueOf(listItem.text.charAt(1));
                if (stickyListItem == null || !stickyListItem.text.equals(firstLetter)) {
                    stickyListItem = new StickyListItem(firstLetter);
                    mListItems.add(i, stickyListItem);
                    size += 1;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public boolean isPinnedViewType(int viewType) {
            return viewType == VIEW_TYPE_STICKY;
        }
    }

    private static class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private TextView tv_content;

        GroupViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }

        void bind(ListItem item) {
            if (item instanceof StickyListItem){
                text.setText(((StickyListItem) item).title);
            }else {
                tv_content.setText(item.text);
            }

        }
    }

    private static class ListItem {

        protected String text;

        ListItem(String text) {
            this.text = text;
        }
    }

    private static class StickyListItem extends ListItem {
        protected String title;
        public StickyListItem(String text) {
            super(text);
            this.title="第"+text+"组";
        }
    }

    protected List<String> createDataList() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            strings.add("第" + i + "个Item");
        }
        return strings;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
