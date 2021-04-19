package com.jelly.baselibrary.social;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.baselibrary.BaseAdapter;
import com.jelly.baselibrary.applicationUtil.AppUtils;

import java.util.List;

public class ShareAdapter extends BaseAdapter<ShareAdapter.ViewHolder> {
    private Context context;
    private List<? extends ShareItem> mList;

    public ShareAdapter(Context context, List<? extends ShareItem> mList) {
        super(context);
        this.context=context;
        this.mList=mList;
    }
    @Override
    public void notifyDataSetChanged(List mList){
        this.mList=mList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        ImageView imageView=new ImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AppUtils.dipTopx(context,50),
                AppUtils.dipTopx(context,50));
        lp.setMargins(AppUtils.dipTopx(context,20), AppUtils.dipTopx(context,20),
                AppUtils.dipTopx(context,20), AppUtils.dipTopx(context,5));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(lp);

        linearLayout.addView(imageView);

        TextView textView=new TextView(context);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lpy.setMargins(0, 0, 0, AppUtils.dipTopx(context,20));
        textView.setLayoutParams(lpy);
        textView.setTextColor(Color.parseColor("#91a9ac"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        linearLayout.addView(textView);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,
                mList.get(position).getIcon()));
        holder.textView.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView= (ImageView)((LinearLayout) itemView).getChildAt(0);
            textView= (TextView)((LinearLayout) itemView).getChildAt(1);
        }
    }
}
