package com.jelly.baselibrary.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.mylhyl.circledialog.AbsBaseCircleDialog;

/**
 * 隐私政策弹窗
 */
public class PrivacyDialog extends AbsBaseCircleDialog implements View.OnClickListener {
    private TextView textView;
    private String details;//内容
    private OnClickListener onClickListener;
    public static PrivacyDialog getInstance() {
        PrivacyDialog dialogFragment = new PrivacyDialog();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.7f);
        dialogFragment.setMaxHeight(0.5f);
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
        //标题
        LinearLayout.LayoutParams titleParams=  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT, 0f);
        titleParams.setMargins(dp2px(context, 0f)
                , dp2px(context, 15f)
                , dp2px(context, 0f)
                , dp2px(context, 15f));
        TextView title = new TextView(context);
        title.setId(1);
        title.setText("隐私保护提示");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#FF000000"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        linearLayout.addView(title, titleParams);

        ScrollView scrollView=new ScrollView(context);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT,1);
        //文本
        textView = new TextView(context);
        textView.setId(0);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT,1);
        textParams.setMargins(dp2px(context, 15f)
                , dp2px(context, 0f)
                , dp2px(context, 15f)
                , dp2px(context, 0f));
        textView.setTextColor(Color.parseColor("#FF000000"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textView.setText(Html.fromHtml("<p style=\"text-align:center;a:link {font-size: 12px;color: #000000;text-decoration: none;} a:visited {font-size: 12px; color: #000000; text-decoration: none;}\" class=\"p\"><span style=\"font-size:14px\">亲，感谢您信任并使用江平生物订货管理系统！我们依据最新的监督要求更新了<a href=\"/1\">《隐私权政策》</a>和<a href=\"/2\">《用户协议》</a>，特向您说明如下:<br/><br/>1.为向您提供交易相关基本功能，我们会收集、使用必要的信息;<br/><br/>2.为向您提供订单动态，优惠活动等信息服务，您需要授权我们获取通知权限，您有权拒绝或取消授权，取消后将不影响您使用我们提供的其他服务;<br/><br/>3.我们会采取业界先进的安全措施保护您的信息安全;<br/><br/>4.未经您的同意，我们不会从第三方处获取、共享或向其提供您的信息</span></p>\n"));
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
                        sp.getSpanStart(url), sp.getSpanEnd(url), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
        scrollView.addView(textView, textParams);
        linearLayout.addView(scrollView,scrollParams);
        //横线
        View line = new View(context);
        line.setBackgroundColor(Color.parseColor("#fff2f2f2"));
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
        left.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        left.setOnClickListener(this);
        bottomLayout.addView(left, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        //竖线
        View line1 = new View(context);
        line1.setBackgroundColor(Color.parseColor("#fff2f2f2"));
        bottomLayout.addView(line1, new LinearLayout.LayoutParams(3, ViewGroup.LayoutParams.MATCH_PARENT));
        //右边按钮
        TextView right = new TextView(context);
        right.setId(2);
        right.setText("已阅读并同意");
        right.setGravity(Gravity.CENTER);
        right.setTextColor(Color.parseColor("#FF000000"));
        right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        right.setOnClickListener(this);

        bottomLayout.addView(right, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        linearLayout.addView(bottomLayout
                , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 45f)));
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
                dismiss();
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
            if (url.equals("/2")){
                return;
            }
            if (TextUtils.isEmpty(details))return;
            PrivacyReadDialog privacyDialog = PrivacyReadDialog.getInstance();
            privacyDialog.show(getChildFragmentManager(), "PrivacyDialog");
        }
    }
    public interface OnClickListener{
        public void onAgree();
        public void onRefuse();
    }
}
