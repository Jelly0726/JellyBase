package com.base;

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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.circledialog.AbsBaseCircleDialog;

/**
 * 阅读隐私政策弹窗
 */
public class PrivacyReadDialog extends AbsBaseCircleDialog implements View.OnClickListener {
    private TextView textView;
    private String details;//内容
    private OnClickListener onClickListener;
    public static PrivacyReadDialog getInstance() {
        PrivacyReadDialog dialogFragment = new PrivacyReadDialog();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        dialogFragment.setMaxHeight(1f);
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
        linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //标题
        LinearLayout.LayoutParams titleParams=  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT, 0f);
        titleParams.setMargins(dp2px(context, 0f)
                , dp2px(context, 15f)
                , dp2px(context, 0f)
                , dp2px(context, 15f));
        TextView title = new TextView(context);
        title.setId(1);
        title.setText("隐私保护协议");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#FF000000"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        linearLayout.addView(title, titleParams);
        ScrollView scrollView=new ScrollView(context);
        scrollView.setBackgroundColor(Color.parseColor("#FFF2F2F2"));
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT,1);
        //文本
        textView = new TextView(context);
        textView.setId(0);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        textParams.setMargins(dp2px(context, 15f)
                , dp2px(context, 0f)
                , dp2px(context, 15f)
                , dp2px(context, 0f));
        textView.setTextColor(Color.parseColor("#FF000000"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        textView.setText(Html.fromHtml("<p style=\"text-align:center;a:link {font-size: 12px;color: #000000;text-decoration: none;} a:visited {font-size: 12px; color: #000000; text-decoration: none;}\" class=\"p\"><span style=\"font-size:15px\">销售订单管理系统隐私权政策</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">销售订单管理系统尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更有个性化的服务，销售订单管理系统会按照本隐私权政策的规定使用和披露您的个人信息。但销售订单管理系统将以高度的勤勉、审慎义务对待这些信息。除本隐私权政策另有规定外，在未征得您事先许可的情况下，销售订单管理系统不会将这些信息对外披露或向第三方提供。销售订单管理系统会不时更新本隐私权政策。您在同意销售订单管理系统服务使用协议之时，即视为您已经同意本隐私权政策全部内容。本隐私权政策属于销售订单管理系统服务使用协议不可分割的一部分。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">1. 适用范围</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">a) 在您注册销售订单管理系统帐号时，您根据销售订单管理系统要求提供的个人注册信息；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">b) 在您使用销售订单管理系统网络服务，或访问销售订单管理系统网页时，销售订单管理系统自动接收并记录的您的浏览器和计算机上的信息，包括但不限于您的IP地址、浏览器的类型、使用的语言、访问日期和时间、软硬件特征信息及您需求的网页记录等数据；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">c) 销售订单管理系统通过合法途径从商业伙伴处取得的用户个人数据。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">您了解并同意，以下信息不适用本隐私权政策：</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">a) 您在使用销售订单管理系统平台提供的搜索服务时输入的关键字信息；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">b) 销售订单管理系统收集到的您在销售订单管理系统发布的有关信息数据，包括但不限于参与活动、成交信息及评价详情；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">c) 违反法律规定或违反销售订单管理系统规则行为及销售订单管理系统已对您采取的措施。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">2. 信息使用</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">a) 销售订单管理系统不会向任何无关第三方提供、出售、出租、分享或交易您的个人信息，除非事先得到您的许可，或该第三方和销售订单管理系统（含销售订单管理系统关联公司）单独或共同为您提供服务，且在该服务结束后，其将被禁止访问包括其以前能够访问的所有这些资料。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">b) 销售订单管理系统亦不允许任何第三方以任何手段收集、编辑、出售或者无偿传播您的个人信息。任何销售订单管理系统平台用户如从事上述活动，一经发现，销售订单管理系统有权立即终止与该用户的服务协议。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">c) 为服务用户的目的，销售订单管理系统可能通过使用您的个人信息，向您提供您感兴趣的信息，包括但不限于向您发出产品和服务信息，或者与销售订单管理系统合作伙伴共享信息以便他们向您发送有关其产品和服务的信息（后者需要您的事先同意）。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">3. 信息披露</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">在如下情况下，销售订单管理系统将依据您的个人意愿或法律的规定全部或部分的披露您的个人信息：</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">a) 经您事先同意，向第三方披露；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">b) 为提供您所要求的产品和服务，而必须和第三方分享您的个人信息；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">c) 根据法律的有关规定，或者行政或司法机构的要求，向第三方或者行政、司法机构披露；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">d) 如您出现违反中国有关法律、法规或者销售订单管理系统服务协议或相关规则的情况，需要向第三方披露；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">e) 如您是适格的知识产权投诉人并已提起投诉，应被投诉人要求，向被投诉人披露，以便双方处理可能的权利纠纷；</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">f) 在销售订单管理系统平台上创建的某一交易中，如交易任何一方履行或部分履行了交易义务并提出信息披露请求的，销售订单管理系统有权决定向该用户提供其交易对方的联络方式等必要信息，以促成交易的完成或纠纷的解决。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">g) 其它销售订单管理系统根据法律、法规或者网站政策认为合适的披露。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">4. 信息存储和交换</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">销售订单管理系统收集的有关您的信息和资料将保存在销售订单管理系统及（或）其关联公司的服务器上，这些信息和资料可能传送至您所在国家、地区或销售订单管理系统收集信息和资料所在地的境外并在境外被访问、存储和展示。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">5. Cookie的使用</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">a) 在您未拒绝接受cookies的情况下，销售订单管理系统会在您的计算机上设定或取用销售订单管理系统</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">，以便您能登录或使用依赖于cookies的销售订单管理系统平台服务或功能。销售订单管理系统使用cookies可为您提供更加周到的个性化服务，包括推广服务。  b) 您有权选择接受或拒绝接受cookies。您可以通过修改浏览器设置的方式拒绝接受cookies。但如果您选择拒绝接受cookies，则您可能无法登录或使用依赖于cookies的销售订单管理系统网络服务或功能。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">c) 通过销售订单管理系统所设cookies所取得的有关信息，将适用本政策。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">6. 信息安全</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">a) 销售订单管理系统帐号均有安全保护功能，请妥善保管您的用户名及密码信息。Poposoft将通过对用户密码进行加密等安全措施确保您的信息不丢失，不被滥用和变造。尽管有前述安全措施，但同时也请您注意在信息网络上不存在“完善的安全措施”。</span></p><p class=\"p\"><span style=\"font-size:12ptpx\">b) 在使用销售订单管理系统网络服务进行网上交易时，您不可避免的要向交易对方或潜在的交易对方披露自己的个人信息，如联络方式或者邮政地址。请您妥善保护自己的个人信息，仅在必要的情形下向他人提供。如您发现自己的个人信息泄密，尤其是销售订单管理系统用户名及密码发生泄露，请您立即联络销售订单管理系统客服，以便销售订单管理系统采取相应措施。</span></p><p style=\"text-align:left;\" class=\"MsoNormal\"><br/><br/></p><p class=\"MsoNormal\"> </p>"));
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
        line.setBackgroundColor(Color.parseColor("#f2f2f2"));
        linearLayout.addView(line, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
        //底部按钮布局
        LinearLayout bottomLayout = new LinearLayout(context);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        //右边按钮
        TextView right = new TextView(context);
        right.setId(2);
        right.setText("已阅读");
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
            case 2://同意
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
            Toast.makeText(getActivity(),"click link="+url,Toast.LENGTH_SHORT).show();
        }
    }
    public interface OnClickListener{
        public void onAgree();
        public void onRefuse();
    }
}
