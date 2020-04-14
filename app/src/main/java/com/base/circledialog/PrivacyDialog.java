package com.base.circledialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 隐私政策弹窗
 */
public class PrivacyDialog extends BaseCircleDialog implements View.OnClickListener {
    private TextView textView;
    private String details;//内容
    private OnClickListener onClickListener;
    public static PrivacyDialog getInstance() {
        PrivacyDialog dialogFragment = new PrivacyDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.6f);
        return dialogFragment;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        //最外层布局
        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        //里层布局
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackground(shapeSolid(context));
        //文本
        textView = new TextView(context);
        textView.setId(0);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        textParams.setMargins(dp2px(context, 15f)
                , dp2px(context, 15f)
                , dp2px(context, 15f)
                , dp2px(context, 15f));
        textView.setTextColor(Color.parseColor("#FF000000"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        textView.setText(Html.fromHtml("<b>text3:</b>  " +
                "Text with a " + "<a href=\"http://www.baidu.com\">link</a> " +
                "点击这个连接---" +
                "<a href=\"http://www.dewen.io/q/1744\">连接</a> " +
                "created in the Java source code using HTML."));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence str = textView.getText();
        if (str instanceof Spannable) {
            int end = str.length();
            Spannable sp = (Spannable) textView.getText();  //构建Spannable对象、继承Spanned、Spanned对象继承CharSequener
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);  //找出text中的a标签
            //SpannableStringBuilder、SpannableString对象跟String对象差不多、只是比String对象多setSpan，
            //可以给字符串设置样式、大小、背景色...而 SpannableStringBuilder跟SpannableString的关系就跟String跟StringBuffer关系一样
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            style.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                MyClickSpan clickSpan=new MyClickSpan(url.getURL());
                //设置样式其中参数what是具体样式的实现对象，start则是该样式开始的位置，end对应的是样式结束的位置，
                // 参数 flags，定义在Spannable中的常量
                //设置点击
                style.setSpan(clickSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //设置前景色
                style.setSpan(new ForegroundColorSpan(Color.parseColor("#0AC3BC")),
                        sp.getSpanStart(url), sp.getSpanEnd(url) + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
        linearLayout.addView(textView, textParams);
        //横线
        View line = new View(context);
        line.setBackgroundColor(Color.parseColor("#fff4fafb"));
        linearLayout.addView(line, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
        //底部按钮布局
        LinearLayout bottomLayout = new LinearLayout(context);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        //左边按钮
        TextView left = new TextView(context);
        left.setId(1);
        left.setText("暂不使用");
        left.setGravity(Gravity.CENTER);
        left.setTextColor(Color.parseColor("#FF000000"));
        left.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        left.setOnClickListener(this);
        //右边按钮
        TextView right = new TextView(context);
        right.setId(2);
        right.setText("同意");
        right.setGravity(Gravity.CENTER);
        right.setTextColor(Color.parseColor("#FF000000"));
        right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        right.setOnClickListener(this);

        bottomLayout.addView(left, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        bottomLayout.addView(right, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        linearLayout.addView(bottomLayout
                , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 40f)));
        rootLayout.addView(linearLayout, layoutParams);
        return rootLayout;
    }

    /**
     * 设置圆角的背景
     *
     * @param context 上下文
     */
    private GradientDrawable shapeSolid(Context context) {
        GradientDrawable gd = new GradientDrawable();
        int strokeWidth = 1; // 1dp 边框宽度
        int roundRadius = 5; // 8dp 圆角半径
        int strokeColor = 0xffffffff;//边框颜色
        int fillColor = 0xffffffff;//内部填充颜色
        gd.setColor(fillColor);
        gd.setCornerRadius(dp2px(context, roundRadius));
        gd.setStroke(dp2px(context, strokeWidth), strokeColor);
        return gd;
    }

    /**
     * 根据手机的分辨率dp 转成px(像素)
     */
    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 1://拒绝
                if (onClickListener!=null)
                    onClickListener.onRefuse();
                dismiss();
                break;
            case 2://同意
                if (onClickListener!=null)
                    onClickListener.onAgree();
                break;
        }
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        if (getActivity() != null && textView != null) {
            textView.setText(Html.fromHtml(details));
        }
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private class MyClickSpan extends ClickableSpan{
        private  String url;
        public MyClickSpan(String url){
            this.url=url;
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
//            super.updateDrawState(ds);
        }
        @Override
        public void onClick(View widget) {
            Toast.makeText(getActivity(),"click link="+url,Toast.LENGTH_SHORT).show();
        }
    }
    public interface OnClickListener{
        public void onAgree();
        public void onRefuse();
    }
}
