package com.base.social;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.AppUtils;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;

import java.util.List;

public class ShareAdapter extends BaseRecyclerAdapter<ShareAdapter.ViewHolder> {
    private Context context;
    private List<? extends ShareItem> mList;

    public ShareAdapter(Context context, List<? extends ShareItem> mList) {
        this.context=context;
        this.mList=mList;
    }
    public void setData(List<? extends ShareItem> mList){
        this.mList=mList;
        notifyDataSetChanged();
    }
    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return mList.size();
    }

    @Override
    public ShareAdapter.ViewHolder getViewHolder(View view) {
        return new ShareAdapter.ViewHolder(view);
    }
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
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
        return new ShareAdapter.ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(ShareAdapter.ViewHolder holder, int position, boolean isItem) {
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,
                mList.get(position).getIcon()));
        holder.textView.setText(mList.get(position).getName());
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
