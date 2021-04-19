package com.jelly.baselibrary.mypopupmenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jelly.baselibrary.R;

import java.util.List;

public class PopupAdapter<T extends BaseItem> extends BaseAdapter {
    private Context myContext;
    private LayoutInflater inflater;
    private List<T> myItems;
    private int myType;

    public PopupAdapter(Context context, List<T> items, int type) {
        this.myContext = context;
        this.myItems = items;
        this.myType = type;

        inflater = LayoutInflater.from(myContext);

    }
    public void setData(List<T> items){
        this.myItems = items;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return myItems.size();
    }

    @Override
    public T getItem(int position) {
        return myItems.get(position);
    }
    private AdapterView.OnItemClickListener mOnItemClickListener;
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PopupHolder holder = null;
        if (convertView == null) {
            holder = new PopupHolder();
            convertView = inflater.inflate(R.layout.top_popup_item, null);
            holder.itemNameTv = (TextView) convertView
                    .findViewById(R.id.popup_tv);
            if (myType == 0) {
                holder.itemNameTv.setGravity(Gravity.CENTER);
            } else if (myType == 1) {
                holder.itemNameTv.setGravity(Gravity.LEFT);
            } else if (myType == 2) {
                holder.itemNameTv.setGravity(Gravity.RIGHT);
            }
            convertView.setTag(holder);
        } else {
            holder = (PopupHolder) convertView.getTag();
        }
        T itemName = getItem(position);
        holder.itemNameTv.setText(((T)itemName).toString());
        if (itemName.isCheck()){
            Drawable img = ContextCompat.getDrawable(myContext,R.drawable.popup_check_24dp);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            holder.itemNameTv.setCompoundDrawables(null, null, img, null);
        }else {
            holder.itemNameTv.setCompoundDrawables(null, null, null, null);
        }
        return convertView;
    }

    private class PopupHolder {
        TextView itemNameTv;
    }

}