package com.jelly.jellybase.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class AutoTextAdapter extends BaseAdapter implements Filterable {
    //除了直接定义数组之外还可以从资源文件中获取数组,获取res/values/arrays文件中数组
//private String[] emails = getResources().getStringArray(R.array.emails);
    private String[] emails = {"@qq.com","@163.com","@sina.com","@gmail.com"};
    private ArrayList<String> data = new ArrayList<>();
    private Context context;
    public AutoTextAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if(convertView == null){
            textView = new TextView(context);
        }else{
            textView = (TextView)convertView;
        }
        textView.setText(data.get(position));
        return textView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<String> newData = new ArrayList<>();
                if(constraint != null && !constraint.toString().contains("@")){
                    for(String data : emails){
                        newData.add(constraint+data);
                    }
                }
                results.values = newData;
                results.count = newData.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList)results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
