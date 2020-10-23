package com.base.dialog;

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
        dialogFragment.setWidth(0.8f);
        dialogFragment.setMaxHeight(0.7f);
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
                , dp2px(context, 0f));
        TextView title = new TextView(context);
        title.setId(1);
        title.setText("隐私协议政策");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#FF000000"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        linearLayout.addView(title, titleParams);
        //文本
        textView = new TextView(context);
        textView.setId(0);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT,1);
        textParams.setMargins(dp2px(context, 15f)
                , dp2px(context, 15f)
                , dp2px(context, 15f)
                , dp2px(context, 15f));
        textView.setTextColor(Color.parseColor("#FF000000"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        textView.setText(Html.fromHtml("使用第三方应用\n" +
                "这种查看内容效果时最好的，这个需要提示用户下载第三方，你可以写连接到应用市场的代码，也可以直接提示让用户自己下载\n" +
                "下载完成之后使用下面代码调用可以读取 doc 或 docx 文件的程序\n" +
                "\n" +
                "Intent intent = getWordFileIntent(wordInfo.getPath());\n" +
                "try {\n" +
                "      getContext().startActivity(intent);\n" +
                "}catch (Exception e) {\n" +
                "      Toast.makeText(getContext(),\"找不到可以打开该文件的程序\",Toast.LENGTH_SHORT).show();\n" +
                "                }\n" +
                "\n" +
                "//android获取一个用于打开Word文件的intent\n" +
                "    public static Intent getWordFileIntent(String param )\n" +
                "    {\n" +
                "        Intent intent = new Intent(\"android.intent.action.VIEW\");\n" +
                "        intent.addCategory(\"android.intent.category.DEFAULT\");\n" +
                "        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);\n" +
                "        Uri uri = Uri.fromFile(new File(param ));\n" +
                "        intent.setDataAndType(uri, \"application/msword\");\n" +
                "        return intent;\n" +
                "    }\n" +
                "调用\n" +
                "\n" +
                "Button open = (Button) findViewById(R.id.open);\n" +
                "        content = (WebView) findViewById(R.id.content);\n" +
                "        WebSettings settings = content.getSettings();\n" +
                "        settings.setUseWideViewPort(true);\n" +
                "        settings.setLoadWithOverviewMode(true);\n" +
                "        settings.setSupportZoom(true);\n" +
                "        settings.setBuiltInZoomControls(true);// 设置WebView可触摸放大缩小\n" +
                "        settings.setUseWideViewPort(true);\n" +
                "\n" +
                "        open.setOnClickListener(new OnClickListener() {\n" +
                "\n" +
                "            @Override\n" +
                "            public void onClick(View v) {\n" +
                "                String path = Environment.getExternalStorageDirectory()\n" +
                "                        + \"/xx/a.docx\";\n" +
                "                Log.d(TAG, \"path=\" + path);\n" +
                "                // tm-extractors-0.4.jar与poi的包在编译时会冲突，二者只能同时导入一个\n" +
                "                WordUtil wu = new WordUtil(path);\n" +
                "                Log.d(TAG, \"htmlPath=\" + wu.htmlPath);\n" +
                "                content.loadUrl(\"file:///\" + wu.htmlPath);\n" +
                "            }\n" +
                "        });\n" +
                "答案参考\n" +
                "http://blog.csdn.net/liubo253/article/details/54614886\n" +
                "http://blog.csdn.net/aqi00/article/details/69942521#comments"));
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
        left.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        left.setOnClickListener(this);
        bottomLayout.addView(left, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        //竖线
        View line1 = new View(context);
        line1.setBackgroundColor(Color.parseColor("#fff4fafb"));
        bottomLayout.addView(line1, new LinearLayout.LayoutParams(3, ViewGroup.LayoutParams.MATCH_PARENT,0f));
        //右边按钮
        TextView right = new TextView(context);
        right.setId(2);
        right.setText("同意");
        right.setGravity(Gravity.CENTER);
        right.setTextColor(Color.parseColor("#FF000000"));
        right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        right.setOnClickListener(this);

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
